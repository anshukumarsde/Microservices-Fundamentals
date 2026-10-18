package com.example.orders.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    TopicExchange domainEventsExchange() {
        return new TopicExchange("domain.events", true, false);
    }
}
