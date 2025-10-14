package org.example.client_processing.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.client_processing.enums.roles.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Ivakov Andrey
 * Сервис для работы с JWT токенами.
 * Обеспечивает генерацию, валидацию и извлечение информации из JWT токенов.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * Генерирует JWT токен для пользователя.
     *
     * @param username имя пользователя
     * @param role роль пользователя
     * @return JWT токен
     */
    public String generateToken(String username, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());
        return createToken(claims, username);
    }

    /**
     * Создает JWT токен с указанными claims.
     *
     * @param claims дополнительные данные для токена
     * @param subject субъект токена (обычно username)
     * @return JWT токен
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Извлекает имя пользователя из токена.
     *
     * @param token JWT токен
     * @return имя пользователя
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлекает роль пользователя из токена.
     *
     * @param token JWT токен
     * @return роль пользователя
     */
    public UserRole extractRole(String token) {
        String roleStr = extractClaim(token, claims -> claims.get("role", String.class));
        return UserRole.valueOf(roleStr);
    }

    /**
     * Извлекает дату истечения токена.
     *
     * @param token JWT токен
     * @return дата истечения
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает указанный claim из токена.
     *
     * @param token JWT токен
     * @param claimsResolver функция для извлечения claim
     * @param <T> тип возвращаемого значения
     * @return значение claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает все claims из токена.
     *
     * @param token JWT токен
     * @return все claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Проверяет, истек ли токен.
     *
     * @param token JWT токен
     * @return true, если токен истек
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Валидирует токен.
     *
     * @param token JWT токен
     * @param username имя пользователя
     * @return true, если токен валиден
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Генерирует refresh токен.
     *
     * @param username имя пользователя
     * @param role роль пользователя
     * @return refresh токен
     */
    public String generateRefreshToken(String username, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("role", role.name());
        return createToken(claims, username, refreshExpiration);
    }

    /**
     * Проверяет, является ли токен refresh токеном.
     *
     * @param token JWT токен
     * @return true, если это refresh токен
     */
    public boolean isRefreshToken(String token) {
        try {
            String tokenType = extractClaim(token, claims -> claims.get("type", String.class));
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Создает JWT токен с указанными claims и временем жизни.
     *
     * @param claims дополнительные данные для токена
     * @param subject субъект токена (обычно username)
     * @param expiration время жизни токена в миллисекундах
     * @return JWT токен
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Получает ключ для подписи токенов.
     *
     * @return секретный ключ
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
