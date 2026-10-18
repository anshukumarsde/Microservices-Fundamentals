package com.example.orders.dto;

import java.math.BigDecimal;

public record CatalogProduct(Long id, String name, BigDecimal price) {
}
