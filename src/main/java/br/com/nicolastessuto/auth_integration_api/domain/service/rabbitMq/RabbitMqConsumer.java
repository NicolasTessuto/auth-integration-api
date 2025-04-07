package br.com.nicolastessuto.auth_integration_api.domain.service.rabbitMq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqConsumer {

    private String QUEUE_NAME;

    @RabbitListener(queues = "${rabbitmq.hubspot-contact-fallback-queue}")
    public void receiveMessage(String message) {

    }

}
