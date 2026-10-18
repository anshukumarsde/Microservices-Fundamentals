package com.example.inventory;

import com.example.inventory.entity.Stock;
import com.example.inventory.repository.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }

    @Bean
    CommandLineRunner seedStock(StockRepository stock) {
        return args -> {
            if (stock.count() == 0) {
                stock.save(new Stock(1L, 20));
                stock.save(new Stock(2L, 15));
                stock.save(new Stock(3L, 8));
            }
        };
    }
}
