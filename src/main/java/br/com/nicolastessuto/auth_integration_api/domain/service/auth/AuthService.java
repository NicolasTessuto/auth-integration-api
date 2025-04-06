package br.com.nicolastessuto.auth_integration_api.domain.service.auth;

import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthLinkResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AvailableProvidersResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    AuthLinkResponse getAuthLink(String providerRequest);

    AvailableProvidersResponse getAvailableProviders();

    ResponseEntity<Void> generateTokens(String code, String provider, HttpServletRequest httpServletRequest);

}
