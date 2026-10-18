package com.example.orders;

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderPlacedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderEventContractTest {
    @Test
    void serializesStableEventContract() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        OrderPlacedEvent event = new OrderPlacedEvent(
                "order.placed",
                Instant.parse("2026-01-01T00:00:00Z"),
                new OrderPlacedEvent.EventData("order-1", 1L, 2)
        );

        String json = objectMapper.writeValueAsString(event);

        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"event_type\":\"order.placed\""));
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"order_id\":\"order-1\""));
        assertEquals(event, objectMapper.readValue(json, OrderPlacedEvent.class));
    }

    @Test
    void acceptsSnakeCaseOrderRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        CreateOrderRequest request = objectMapper.readValue(
                "{\"product_id\":1,\"quantity\":2}", CreateOrderRequest.class
        );

        assertEquals(new CreateOrderRequest(1L, 2), request);
    }
}
