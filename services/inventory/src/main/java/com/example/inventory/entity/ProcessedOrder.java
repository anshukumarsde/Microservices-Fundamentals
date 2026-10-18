package com.example.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "processed_orders")
public class ProcessedOrder {
    @Id
    private String orderId;
    private Long productId;
    private Integer quantity;
    private String status;
    private Instant processedAt;

    protected ProcessedOrder() {
    }

    public ProcessedOrder(String orderId, Long productId, Integer quantity, String status) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.processedAt = Instant.now();
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}
