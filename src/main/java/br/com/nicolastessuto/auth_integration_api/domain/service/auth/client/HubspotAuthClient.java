package br.com.nicolastessuto.auth_integration_api.domain.service.auth.client;

import br.com.nicolastessuto.auth_integration_api.domain.service.auth.GenericAuthClient;
import br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.AuthTokenIntegrationResponse;
import br.com.nicolastessuto.auth_integration_api.domain.service.userSession.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class HubspotAuthClient implements GenericAuthClient {

    @Value("${authentication-provider.hubspot.application.id}")
    private String HUBSPOT_APPLICATION_ID;

    @Value("${authentication-provider.hubspot.application.client-id}")
    private String HUBSPOT_CLIENT_ID;

    @Value("${authentication-provider.hubspot.application.client-secret}")
    private String HUBSPOT_CLIENT_SECRET;

    @Value("${authentication-provider.hubspot.application.redirect-uri}")
    private String HUBSPOT_REDIRECT_URI;

    @Value("${authentication-provider.hubspot.application.exchange-for-token-url}")
    private String HUBSPOT_EXCHANGE_FOR_TOKEN_URI;

    private final WebClient webClient;
    private final UserSessionService userSessionService;

    @Override
    public String getAuthLink() {

        String HUBSPOT_AUTH_BASE_LINK = "https://app.hubspot.com/oauth/authorize?" +
                "client_id=%s&scope=oauth&redirect_uri=%s" +
                "&state=HUBSPOT";

        return String.format(String.format(HUBSPOT_AUTH_BASE_LINK, HUBSPOT_CLIENT_ID, HUBSPOT_REDIRECT_URI));
    }

    @Override
    public ResponseEntity<Void> generateAuthTokenAndRefreshToken(String code, HttpServletRequest httpServletRequest) {
        String sessionId = httpServletRequest.getSession().getId();
        Mono<AuthTokenIntegrationResponse> response = generateIntegrationAndAuthorize(code, false, sessionId);

        AuthTokenIntegrationResponse authTokenResponse = response.block();
        if (authTokenResponse != null) {
            userSessionService.saveUserSession(
                    sessionId,
                    authTokenResponse.refreshToken(),
                    authTokenResponse.expiresIn()
            );
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/home"));
        return new ResponseEntity<>(headers, HttpStatus.valueOf(200));
    }

    private Mono<AuthTokenIntegrationResponse> generateIntegrationAndAuthorize(String code,
                                                                               Boolean hasToUpdateToken,
                                                                               String sessionId) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("redirect_uri", HUBSPOT_REDIRECT_URI);
        formData.add("client_id", HUBSPOT_CLIENT_ID);
        formData.add("client_secret", HUBSPOT_CLIENT_SECRET);

        if (hasToUpdateToken) {
            formData.add("grant_type",  "refresh_token");
            formData.add("refresh_token", userSessionService.getRefreshTokenBySessionId(sessionId));
        } else {
            formData.add("grant_type", "authorization_code");
            formData.add("code", code);
        }

        return webClient.post()
                        .uri(HUBSPOT_EXCHANGE_FOR_TOKEN_URI)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .bodyValue(formData)
                        .retrieve()
                        .bodyToMono(AuthTokenIntegrationResponse.class);
    }

}
