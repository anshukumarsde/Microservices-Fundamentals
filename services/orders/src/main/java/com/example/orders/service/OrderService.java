package com.example.orders.service;

import com.example.orders.dto.CatalogProduct;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderPlacedEvent;
import com.example.orders.entity.OrderRecord;
import com.example.orders.entity.OutboxMessage;
import com.example.orders.repository.OrderRepository;
import com.example.orders.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final OutboxRepository outbox;
    private final RestClient catalogClient;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orders,
                        OutboxRepository outbox,
                        RestClient.Builder restClientBuilder,
                        ObjectMapper objectMapper,
                        @Value("${catalog.url:http://localhost:8001}") String catalogUrl) {
        this.orders = orders;
        this.outbox = outbox;
        this.catalogClient = restClientBuilder.baseUrl(catalogUrl).build();
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderRecord placeOrder(CreateOrderRequest request) {
        CatalogProduct product;
        try {
            product = catalogClient.get()
                    .uri("/products/{id}", request.productId())
                    .retrieve()
                    .body(CatalogProduct.class);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unknown product_id");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Catalog service is unavailable");
        }
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Catalog returned an empty response");
        }

        Instant now = Instant.now();
        String orderId = UUID.randomUUID().toString();
        OrderRecord order = new OrderRecord(
                orderId, product.id(), product.name(), request.quantity(),
                product.price(), "accepted", now
        );
        OrderPlacedEvent event = new OrderPlacedEvent(
                "order.placed",
                now,
                new OrderPlacedEvent.EventData(orderId, product.id(), request.quantity())
        );
        try {
            outbox.save(new OutboxMessage(orderId, objectMapper.writeValueAsString(event)));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize order event", exception);
        }
        return orders.save(order);
    }

    public OrderRecord getOrder(String id) {
        return orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }
}
