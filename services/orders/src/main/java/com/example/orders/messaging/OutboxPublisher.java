package com.example.orders.messaging;

import com.example.orders.entity.OutboxMessage;
import com.example.orders.repository.OutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class OutboxPublisher {
    private final OutboxRepository outbox;
    private final RabbitTemplate rabbitTemplate;

    public OutboxPublisher(OutboxRepository outbox, RabbitTemplate rabbitTemplate) {
        this.outbox = outbox;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelayString = "${outbox.publish-delay-ms:1000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxMessage> pending = outbox.findTop20ByPublishedFalseOrderByIdAsc();
        for (OutboxMessage message : pending) {
            MessageProperties properties = new MessageProperties();
            properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            Message event = new Message(
                    message.getPayload().getBytes(StandardCharsets.UTF_8), properties
            );
            rabbitTemplate.send("domain.events", "order.placed", event);
            message.markPublished();
        }
    }
}
