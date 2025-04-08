package br.com.nicolastessuto.auth_integration_api.domain.service.auth;

import br.com.nicolastessuto.auth_integration_api.domain.auth.Provider;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.client.HubspotAuthClient;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AuthFactory {

    private static ApplicationContext context;

    public AuthFactory(ApplicationContext context) {
        AuthFactory.context = context;
    }

    public static GenericAuthClient getAuthClientProvider(Provider provider) {
        return switch (provider) {
            case HUBSPOT -> context.getBean(HubspotAuthClient.class);
        };
    }

}
