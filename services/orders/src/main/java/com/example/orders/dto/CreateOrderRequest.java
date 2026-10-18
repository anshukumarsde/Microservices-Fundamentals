package com.example.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
        @JsonProperty("product_id") @NotNull @Positive Long productId,
        @JsonProperty("quantity") @NotNull @Min(1) @Max(1000) Integer quantity
) {
}
