package br.com.nicolastessuto.auth_integration_api.domain.service.rabbitMq.consumer;

import br.com.nicolastessuto.auth_integration_api.domain.service.contact.client.HubspotContactClient;
import br.com.nicolastessuto.auth_integration_api.domain.service.contact.request.integration.ContactIntegrationMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMqConsumer {

    private final HubspotContactClient hubspotContactClient;

    @RabbitListener(
            queues = "${rabbitmq.hubspot-contact-fallback-queue}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void receiveMessage(ContactIntegrationMessageRequest message) {
        hubspotContactClient.createContact(message);
    }

}
