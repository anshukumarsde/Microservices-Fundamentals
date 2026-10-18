package com.example.inventory;

import com.example.inventory.dto.OrderPlacedEvent;
import com.example.inventory.entity.ProcessedOrder;
import com.example.inventory.entity.Stock;
import com.example.inventory.repository.ProcessedOrderRepository;
import com.example.inventory.repository.StockRepository;
import com.example.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock
    private StockRepository stock;
    @Mock
    private ProcessedOrderRepository processedOrders;
    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void reservesStockOnlyOnceForDuplicateEvent() {
        OrderPlacedEvent event = event("order-1", 1L, 3);
        Stock coffee = new Stock(1L, 20);
        when(processedOrders.findById("order-1")).thenReturn(Optional.empty(), Optional.of(
                new ProcessedOrder("order-1", 1L, 3, "reserved")
        ));
        when(stock.findById(1L)).thenReturn(Optional.of(coffee));

        assertEquals("reserved", inventoryService.processOrder(event));
        assertEquals("reserved", inventoryService.processOrder(event));

        assertEquals(17, coffee.getQuantity());
        verify(stock).save(coffee);
        verify(processedOrders).save(any(ProcessedOrder.class));
    }

    @Test
    void rejectsOrderWhenStockIsInsufficient() {
        when(processedOrders.findById("order-2")).thenReturn(Optional.empty());
        when(stock.findById(3L)).thenReturn(Optional.of(new Stock(3L, 8)));

        assertEquals("rejected", inventoryService.processOrder(event("order-2", 3L, 100)));

        verify(stock, never()).save(any());
    }

    private OrderPlacedEvent event(String orderId, Long productId, Integer quantity) {
        return new OrderPlacedEvent(
                "order.placed",
                Instant.now(),
                new OrderPlacedEvent.EventData(orderId, productId, quantity)
        );
    }
}
