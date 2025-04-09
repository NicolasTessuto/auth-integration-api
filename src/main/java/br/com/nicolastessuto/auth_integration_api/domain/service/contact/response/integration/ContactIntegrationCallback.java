package br.com.nicolastessuto.auth_integration_api.domain.service.contact.response.integration;

public record ContactIntegrationCallback(
        long eventId,
        long subscriptionId,
        long portalId,
        long appId,
        long occurredAt,
        String subscriptionType,
        int attemptNumber,
        long objectId,
        String changeFlag,
        String changeSource,
        String sourceId
) {
}

