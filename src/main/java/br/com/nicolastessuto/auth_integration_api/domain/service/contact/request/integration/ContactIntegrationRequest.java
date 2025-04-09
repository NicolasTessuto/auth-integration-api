package br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.integration;

import lombok.Builder;

@Builder
public record ContactIntegrationRequest(
        ContactDataIntegrationRequest properties
) {
}
