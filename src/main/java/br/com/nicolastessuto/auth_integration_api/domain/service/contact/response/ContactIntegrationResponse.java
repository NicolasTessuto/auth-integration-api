package br.com.nicolastessuto.auth_integration_api.domain.service.contact.response;

import lombok.Builder;

@Builder
public record ContactIntegrationResponse(
        ContactDataIntegrationResponse properties
) {
}
