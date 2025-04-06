package br.com.nicolastessuto.auth_integration_api.domain.service.userSession;

public interface UserSessionService {
    void saveUserSession(String id, String refreshToken, Integer expiresIn);

    String getRefreshTokenBySessionId(String sessionId);
}
