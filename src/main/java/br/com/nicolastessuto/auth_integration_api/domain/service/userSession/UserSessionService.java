package br.com.nicolastessuto.auth_integration_api.domain.service.userSession;

import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthTokenIntegrationResponse;

public interface UserSessionService {
    void saveUserSession(String id, AuthTokenIntegrationResponse authTokenIntegrationResponse);

    String getRefreshTokenBySessionId(String sessionId);

    String getRefreshTokenByOldToken(String authorization);
}
