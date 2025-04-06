package br.com.nicolastessuto.auth_integration_api.domain.service.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthCodeProofIntegrationRequest (

        @JsonProperty("grant_type")
        String grantType,

        @JsonProperty("client_id")
        String clientId,

        @JsonProperty("client_secret")
        String client_secret,

        @JsonProperty("redirect_uri")
        String redirect_uri,

        String code
) {}

