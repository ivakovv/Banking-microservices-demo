package org.example.client_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client_processing.dto.auth.LoginRequest;
import org.example.client_processing.dto.auth.LoginResponse;
import org.example.client_processing.dto.auth.RefreshTokenRequest;
import org.example.client_processing.dto.auth.RefreshTokenResponse;
import org.example.client_processing.enums.roles.UserRole;
import org.example.client_processing.model.User;
import org.example.client_processing.repository.UserRepository;
import org.example.client_processing.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private long accessTokenExpiration;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (user.getRole() == UserRole.BLOCKED_CLIENT) {
            throw new IllegalStateException("Account is blocked. Please contact support.");
        }

        String accessToken = jwtService.generateToken(user.getLogin(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getLogin(), user.getRole());

        log.info("User {} successfully logged in", user.getLogin());

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                accessTokenExpiration / 1000,
                user.getRole(),
                "Authentication successful"
        );
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtService.isRefreshToken(request.refreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(request.refreshToken());
        UserRole role = jwtService.extractRole(request.refreshToken());

        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() == UserRole.BLOCKED_CLIENT) {
            throw new IllegalStateException("Account is blocked. Please contact support.");
        }

        String newAccessToken = jwtService.generateToken(username, role);

        log.info("Access token refreshed for user {}", username);

        return new RefreshTokenResponse(
                newAccessToken,
                "Bearer",
                accessTokenExpiration / 1000,
                "Token refreshed successfully"
        );
    }
}
