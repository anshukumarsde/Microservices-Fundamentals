package com.example.inventory.controller;

import com.example.inventory.entity.Stock;
import com.example.inventory.repository.ProcessedOrderRepository;
import com.example.inventory.repository.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class InventoryController {
    private final StockRepository stock;
    private final ProcessedOrderRepository processedOrders;

    public InventoryController(StockRepository stock, ProcessedOrderRepository processedOrders) {
        this.stock = stock;
        this.processedOrders = processedOrders;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "inventory");
    }

    @GetMapping("/inventory")
    public Map<String, Object> getInventory() {
        List<Map<String, Object>> stockItems = stock.findAll().stream()
                .map(item -> Map.<String, Object>of(
                        "product_id", item.getProductId(),
                        "quantity", item.getQuantity()
                ))
                .toList();
        List<Map<String, Object>> orders = processedOrders.findAll().stream()
                .map(order -> Map.<String, Object>of(
                        "order_id", order.getOrderId(),
                        "product_id", order.getProductId(),
                        "quantity", order.getQuantity(),
                        "status", order.getStatus(),
                        "processed_at", order.getProcessedAt()
                ))
                .toList();
        return Map.of("stock", stockItems, "processed_orders", orders);
    }

    @GetMapping("/inventory/{productId}")
    public Map<String, Object> getStock(@PathVariable Long productId) {
        Stock item = stock.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Inventory item not found"
                ));
        return Map.of("product_id", item.getProductId(), "quantity", item.getQuantity());
    }
}
