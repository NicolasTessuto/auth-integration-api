package br.com.nicolastessuto.auth_integration_api.domain.service.auth;

import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface GenericAuthClient {
    String getAuthLink();

    TokenResponse generateAuthTokenAndRefreshToken(String code, HttpServletRequest httpServletRequest);
}
