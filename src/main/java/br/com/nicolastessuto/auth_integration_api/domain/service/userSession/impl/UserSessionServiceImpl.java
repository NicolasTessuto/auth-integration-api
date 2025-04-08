package br.com.nicolastessuto.auth_integration_api.domain.service.userSession.impl;

import br.com.nicolastessuto.auth_integration_api.domain.auth.UserSession;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthTokenIntegrationResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.userSession.UserSessionService;
import br.com.nicolastessuto.auth_integration_api.domain.respository.UserSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;

    @Override
    public void saveUserSession(String id, AuthTokenIntegrationResponse integrationResponse) {

        try {
            findBySessionId(id);
        } catch (EntityNotFoundException ignored) {
            var userSession = UserSession.builder()
                    .sessionId(id)
                    .refreshToken(integrationResponse.refreshToken())
                    .token(integrationResponse.token())
                    .expirationTimestamp(
                            calculateExpiration(integrationResponse.expiresIn())
                    )
                    .build();

            userSessionRepository.save(userSession);
        }

    }

    private Instant calculateExpiration(Integer expiresIn) {
        return Instant.now().plusSeconds((long) (expiresIn * 0.75));
    }

    @Override
    public String getRefreshTokenBySessionId(String sessionId) {
        return findBySessionId(sessionId).getRefreshToken();
    }

    @Override
    public String getRefreshTokenByOldToken(String authorization) {
        return findByCurrentToken(authorization).getRefreshToken();
    }

    private UserSession findByCurrentToken(String authorization) {
        return userSessionRepository.findByToken(authorization).orElseThrow(() -> new EntityNotFoundException("Session not found"));
    }

    public UserSession findBySessionId(String sessionId) {
        var response = userSessionRepository.findBySessionId(sessionId).orElseThrow(() -> new EntityNotFoundException("Session not found"));
        return response;
    }

}
