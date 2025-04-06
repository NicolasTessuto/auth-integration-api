package br.com.nicolastessuto.auth_integration_api.domain.service.auth;

import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthLinkResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AvailableProvidersResponse;

public interface AuthService {
    AuthLinkResponse getAuthLink(String providerRequest);

    AvailableProvidersResponse getAvailableProviders();

}
