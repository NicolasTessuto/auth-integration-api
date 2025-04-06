package br.com.nicolastessuto.auth_integration_api.domain.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface GenericAuthClient {
    String getAuthLink();

    ResponseEntity<Void> generateAuthTokenAndRefreshToken(String code, HttpServletRequest httpServletRequest);
}
