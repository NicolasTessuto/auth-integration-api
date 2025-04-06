package br.com.nicolastessuto.auth_integration_api.domain.service.userSession.impl;

import br.com.nicolastessuto.auth_integration_api.domain.auth.UserSession;
import br.com.nicolastessuto.auth_integration_api.domain.service.userSession.UserSessionService;
import br.com.nicolastessuto.auth_integration_api.respository.UserSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;

    @Override
    public void saveUserSession(String id, String refreshToken, Integer expiresIn) {

        try {
            findBySessionId(id);
        } catch (EntityNotFoundException ignored) {
            var userSession = UserSession.builder()
                    .sessionId(id)
                    .refreshToken(refreshToken)
                    .expirationTimestamp(
                            calculateExpiration(expiresIn)
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

    public UserSession findBySessionId(String sessionId) {
        return userSessionRepository.findBySessionId(sessionId).orElseThrow(() -> new EntityNotFoundException("Session not found"));
    }

}
