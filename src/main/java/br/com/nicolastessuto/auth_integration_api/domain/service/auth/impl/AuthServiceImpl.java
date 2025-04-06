package br.com.nicolastessuto.auth_integration_api.domain.service.auth.impl;

import br.com.nicolastessuto.auth_integration_api.domain.auth.Provider;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.AuthFactory;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.AuthService;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.GenericAuthClient;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthLinkResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AvailableProvidersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public AuthLinkResponse getAuthLink(String providerRequest) {
        Provider provider = validateAndGetProvider(providerRequest);

        GenericAuthClient genericAuthClient = AuthFactory.getAuthServiceProvider(provider);

        return AuthLinkResponse.builder()
                .link(genericAuthClient.getAuthLink())
                .build();
    }

    private Provider validateAndGetProvider(String providerRequest) {
        try {
            return Provider.valueOf(providerRequest.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Provider not supported");
        }
    }

    @Override
    public AvailableProvidersResponse getAvailableProviders() {
        List<String> providers = new ArrayList<>();

        for (Provider provider : Provider.values()){
            providers.add(provider.name());
        }
        return AvailableProvidersResponse.builder()
                .providers(providers)
                .build();
    }

}
