package com.example.orderservice.controller;

import com.example.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderservice.messaging.OrderMessageProducer;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final OrderMessageProducer messageProducer;

    public OrderController(OrderService orderService, OrderMessageProducer messageProducer) {
        this.orderService = orderService;
        this.messageProducer = messageProducer;
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

    // Test: Nur RabbitMQ
    @GetMapping("/rabbit/{id}/{signal}")
    public String testRabbit(@PathVariable String id, @PathVariable String signal) {
        messageProducer.sendToRabbit(id, signal);
        return "An RabbitMQ gesendet: " + signal;
    }

    // Test: Nur Kafka
    @GetMapping("/kafka/{id}/{signal}")
    public String testKafka(@PathVariable String id, @PathVariable String signal) {
        messageProducer.sendToKafka(id, signal);
        return "An Kafka gesendet: " + signal;
    }

    // Test: Beide gleichzeitig + REST-Logik
    @GetMapping("/multi/{id}/{signal}")
    public String testAll(@PathVariable String id, @PathVariable String signal) {
        orderService.processOrderFullStack(id, signal);
        return "Full-Stack Verarbeitung für " + signal + " gestartet!";
    }
}