package br.com.nicolastessuto.auth_integration_api.config.rabbitMq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {
    @Value("${spring.rabbitmq.host}")
    private String HOST;

    @Value("${spring.rabbitmq.port}")
    private int PORT;

    @Value("${spring.rabbitmq.username}")
    private String USERNAME;

    @Value("${spring.rabbitmq.password}")
    private String PASSWORD;

    @Value("${rabbitmq.hubspot-contact-fallback-queue}")
    private String FALLBACK_QUEUE_NAME;

    @Value("${rabbitmq.hubspot-contact-delay-fallback-queue}")
    private String FALLBACK_DELAY_QUEUE_NAME;

    @Value("${rabbitmq.hubspot-contact-fallback-error-queue}")
    private String FALLBACK_DELAY_QUEUE_NAME_ERROR;

    private static final String MAIN_EXCHANGE_NAME = "TopicExchange";
    private static final String DELAY_EXCHANGE_NAME = "DelayExchange";
    private static final String DEAD_LETTER_EXCHANGE_NAME = "DeadLetterExchange";

    private static final String MAIN_ROUTING_KEY = "hubspot.fallback";
    private static final String DELAY_ROUTING_KEY = "hubspot.delay";
    private static final String DLQ_ROUTING_KEY = "hubspot.error";

    @Bean
    public Queue mainQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", DLQ_ROUTING_KEY);
        return new Queue(FALLBACK_QUEUE_NAME, true, false, false, args);
    }

    @Bean
    public Queue delayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 60000);
        args.put("x-dead-letter-exchange", MAIN_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", MAIN_ROUTING_KEY);
        return new Queue(FALLBACK_DELAY_QUEUE_NAME, true, false, false, args);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                               MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }


    @Bean
    public Queue deadLetterQueue() {
        return new Queue(FALLBACK_DELAY_QUEUE_NAME_ERROR, true);
    }

    @Bean
    public TopicExchange mainExchange() {
        return new TopicExchange(MAIN_EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange delayExchange() {
        return new TopicExchange(DELAY_EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DEAD_LETTER_EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingMainQueue() {
        return BindingBuilder.bind(mainQueue())
                .to(mainExchange())
                .with(MAIN_ROUTING_KEY);
    }

    @Bean
    public Binding bindingDelayQueue() {
        return BindingBuilder.bind(delayQueue())
                .to(delayExchange())
                .with(DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

}
