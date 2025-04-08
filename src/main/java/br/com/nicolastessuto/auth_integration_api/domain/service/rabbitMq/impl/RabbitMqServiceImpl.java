package br.com.nicolastessuto.auth_integration_api.domain.service.rabbitMq.impl;

import br.com.nicolastessuto.auth_integration_api.domain.service.rabbitMq.RabbitMqService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMqServiceImpl implements RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishObjectInQueue(String queueName, Object message) {
        rabbitTemplate.convertAndSend(queueName, message);
    }

}
