package br.com.nicolastessuto.auth_integration_api.domain.service.contact.request;

import lombok.Builder;

@Builder
public record ContactIntegrationMessageRequest(
        String authorization,
        ContactIntegrationRequest contactIntegration
) {}
