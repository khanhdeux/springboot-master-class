package com.example.orderservice.controller;

import com.example.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public String createOrder(@PathVariable String id) {
        String user = orderService.checkUserLegacy(id);
        return "Legacy Bestellung für " + user;
    }

    // URL: http://localhost:8081/api/orders/resilient/1
    @GetMapping("/resilient/{id}")
    public String createOrderResilient(@PathVariable String id) {
        String user = orderService.checkUserResilient(id);
        return "Resiliente Bestellung für " + user;
    }
}