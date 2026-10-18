package com.example.inventory.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    TopicExchange domainEventsExchange() {
        return new TopicExchange("domain.events", true, false);
    }

    @Bean
    Queue inventoryOrderPlacedQueue() {
        return new Queue("inventory.order-placed", true);
    }

    @Bean
    Binding inventoryOrderPlacedBinding(Queue inventoryOrderPlacedQueue,
                                        TopicExchange domainEventsExchange) {
        return BindingBuilder.bind(inventoryOrderPlacedQueue)
                .to(domainEventsExchange)
                .with("order.placed");
    }
}
