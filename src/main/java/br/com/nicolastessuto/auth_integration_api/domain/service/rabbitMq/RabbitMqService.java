package br.com.nicolastessuto.auth_integration_api.domain.service.rabbitMq;

public interface RabbitMqService {
    void publishObjectInQueue(String queueName, Object message);
}
