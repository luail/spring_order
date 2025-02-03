package com.example.ordersystem.ordering.service;

import com.example.ordersystem.ordering.repository.OrderingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderingService {
    private final OrderingRepository orderingRepository;

    public OrderingService(OrderingRepository orderingRepository) {
        this.orderingRepository = orderingRepository;
    }
}
