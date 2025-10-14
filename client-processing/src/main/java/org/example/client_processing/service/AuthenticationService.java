package org.example.client_processing.service;

import org.example.client_processing.dto.auth.LoginRequest;
import org.example.client_processing.dto.auth.LoginResponse;
import org.example.client_processing.dto.auth.RefreshTokenRequest;
import org.example.client_processing.dto.auth.RefreshTokenResponse;

/**
 * @author Ivakov Andrey
 * Сервис для аутентификации пользователей.
 */
public interface AuthenticationService {

    /**
     * Аутентифицирует пользователя по логину и паролю.
     *
     * @param request данные для входа
     * @return ответ с токенами и информацией о пользователе
     * @see LoginResponse
     */
    LoginResponse login(LoginRequest request);

    /**
     * Обновляет access токен используя refresh токен.
     *
     * @param request запрос на обновление токена
     * @return новый access токен
     * @see RefreshTokenResponse
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
