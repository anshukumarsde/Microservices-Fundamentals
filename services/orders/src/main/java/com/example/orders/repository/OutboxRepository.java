package com.example.orders.repository;

import com.example.orders.entity.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    List<OutboxMessage> findTop20ByPublishedFalseOrderByIdAsc();
}
