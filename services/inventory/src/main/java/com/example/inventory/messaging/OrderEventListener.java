package com.example.inventory.messaging;

import com.example.inventory.dto.OrderPlacedEvent;
import com.example.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderEventListener {
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    public OrderEventListener(InventoryService inventoryService, ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "inventory.order-placed")
    public void onOrderPlaced(Message message) {
        try {
            OrderPlacedEvent event = objectMapper.readValue(
                    message.getBody(), OrderPlacedEvent.class
            );
            String status = inventoryService.processOrder(event);
            System.out.printf("Order %s inventory status: %s%n", event.data().orderId(), status);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Invalid order.placed event", exception);
        }
    }
}
