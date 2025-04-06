package br.com.nicolastessuto.auth_integration_api.domain.service.auth.impl;

import br.com.nicolastessuto.auth_integration_api.domain.auth.Provider;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.AuthFactory;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.AuthService;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.GenericAuthClient;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthLinkResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AvailableProvidersResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public AuthLinkResponse getAuthLink(String providerRequest) {
        GenericAuthClient genericAuthClient =
                validateProviderAndGetAuthClient(providerRequest);

        return AuthLinkResponse.builder()
                .link(genericAuthClient.getAuthLink())
                .build();
    }

    private GenericAuthClient validateProviderAndGetAuthClient(String providerRequest) {
        Provider provider = validateAndGetProvider(providerRequest);
        return AuthFactory.getAuthServiceProvider(provider);
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

    @Override
    public ResponseEntity<Void> generateTokens(String code, String provider, HttpServletRequest httpServletRequest) {
        GenericAuthClient genericAuthClient = validateProviderAndGetAuthClient(provider);
        return genericAuthClient.generateAuthTokenAndRefreshToken(code, httpServletRequest);
    }

}
