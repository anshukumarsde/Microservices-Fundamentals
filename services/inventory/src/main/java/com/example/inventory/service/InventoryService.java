package com.example.inventory.service;

import com.example.inventory.dto.OrderPlacedEvent;
import com.example.inventory.entity.ProcessedOrder;
import com.example.inventory.entity.Stock;
import com.example.inventory.repository.ProcessedOrderRepository;
import com.example.inventory.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {
    private final StockRepository stock;
    private final ProcessedOrderRepository processedOrders;

    public InventoryService(StockRepository stock, ProcessedOrderRepository processedOrders) {
        this.stock = stock;
        this.processedOrders = processedOrders;
    }

    @Transactional
    public String processOrder(OrderPlacedEvent event) {
        if (event == null || !"order.placed".equals(event.eventType()) || event.data() == null
                || event.data().orderId() == null || event.data().productId() == null
                || event.data().quantity() == null || event.data().quantity() < 1) {
            throw new IllegalArgumentException("Invalid order.placed event");
        }

        return processedOrders.findById(event.data().orderId())
                .map(ProcessedOrder::getStatus)
                .orElseGet(() -> reserveStock(event.data()));
    }

    private String reserveStock(OrderPlacedEvent.EventData data) {
        Stock item = stock.findById(data.productId()).orElse(null);
        String status;
        if (item == null || item.getQuantity() < data.quantity()) {
            status = "rejected";
        } else {
            item.reserve(data.quantity());
            stock.save(item);
            status = "reserved";
        }
        processedOrders.save(new ProcessedOrder(
                data.orderId(), data.productId(), data.quantity(), status
        ));
        return status;
    }
}
