package br.com.nicolastessuto.auth_integration_api.domain.service.auth.client;

import br.com.nicolastessuto.auth_integration_api.domain.service.auth.GenericAuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HubspotAuthClient implements GenericAuthClient {

    @Value("${authentication-provider.hubspot.application.id}")
    private String HUBSPOT_APPLICATION_ID;

    @Value("${authentication-provider.hubspot.application.client-id}")
    private String HUBSPOT_CLIENT_ID;

    @Value("${authentication-provider.hubspot.application.client-secret}")
    private String HUBSPOT_CLIENT_SECRET;

    @Value("${authentication-provider.hubspot.application.redirect-uri}")
    private String HUBSPOT_REDIRECT_URI;

    private String HUBSPOT_AUTH_BASE_LINK =
            "https://app.hubspot.com/oauth/authorize?" +
                    "client_id=%s&scope=oauth&redirect_uri=%s";

    @Override
    public String getAuthLink() {
        return String.format(String.format(HUBSPOT_AUTH_BASE_LINK, HUBSPOT_CLIENT_ID, HUBSPOT_REDIRECT_URI));
    }

}
