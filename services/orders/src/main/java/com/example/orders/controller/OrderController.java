package com.example.orders.controller;

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.entity.OrderRecord;
import com.example.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "orders");
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderRecord> placeOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderRecord order = orderService.placeOrder(request);
        return ResponseEntity.created(URI.create("/orders/" + order.getId()))
                .body(order);
    }

    @GetMapping("/orders/{id}")
    public OrderRecord getOrder(@PathVariable String id) {
        return orderService.getOrder(id);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(
            org.springframework.web.bind.MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, String>> handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Invalid request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
    }
}
