package br.com.nicolastessuto.auth_integration_api.domain.service.auth;

import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthLinkResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AvailableProvidersResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthLinkResponse getAuthLink(String providerRequest);

    AvailableProvidersResponse getAvailableProviders();

    TokenResponse generateTokens(String code, String provider, HttpServletRequest httpServletRequest);
}
