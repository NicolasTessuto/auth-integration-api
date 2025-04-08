package br.com.nicolastessuto.auth_integration_api.domain.service.contact.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContactDataIntegrationResponse(
        String email,
        @JsonProperty(value = "firstname")
        String firstName,
        @JsonProperty(value = "lastname")
        String lastName
) {
}
