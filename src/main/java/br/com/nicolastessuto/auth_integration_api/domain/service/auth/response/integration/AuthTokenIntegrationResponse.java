package br.com.nicolastessuto.auth_integration_api.domain.service.auth.response.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthTokenIntegrationResponse(
        @JsonProperty("access_token")
        String token,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("expires_in")
        Integer expiresIn
) {
}
