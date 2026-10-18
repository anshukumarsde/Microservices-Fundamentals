package com.example.catalog;

import com.example.catalog.entity.Product;
import com.example.catalog.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class CatalogApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);
    }

    @Bean
    CommandLineRunner seedProducts(ProductRepository products) {
        return args -> {
            if (products.count() == 0) {
                products.saveAll(java.util.List.of(
                        new Product(1L, "Coffee", new BigDecimal("12.50")),
                        new Product(2L, "Tea", new BigDecimal("8.00")),
                        new Product(3L, "Mug", new BigDecimal("6.25"))
                ));
            }
        };
    }
}
