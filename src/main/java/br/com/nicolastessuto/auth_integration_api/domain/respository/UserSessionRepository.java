package br.com.nicolastessuto.auth_integration_api.domain.respository;

import br.com.nicolastessuto.auth_integration_api.domain.auth.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {

    Optional<UserSession> findBySessionId(String sessionId);

    Optional<UserSession> findByToken(String authorization);
}
