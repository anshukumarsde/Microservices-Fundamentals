package com.example.inventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record OrderPlacedEvent(
        @JsonProperty("event_type") String eventType,
        @JsonProperty("occurred_at") Instant occurredAt,
        EventData data
) {
    public record EventData(
            @JsonProperty("order_id") String orderId,
            @JsonProperty("product_id") Long productId,
            Integer quantity
    ) {
    }
}
