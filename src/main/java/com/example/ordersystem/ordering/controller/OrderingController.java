package com.example.ordersystem.ordering.controller;

import com.example.ordersystem.ordering.service.OrderingService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderingController {
    private final OrderingService orderingService;

    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }
}
