package com.example.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final RestTemplate restTemplate;

    public OrderController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @GetMapping("/{id}")
    public String createOrder(@PathVariable String id) {
        log.info("Order-Service: Erstelle Bestellung für User {}", id);
        
        // Aufruf an den User-Service (Port 8080)
        String user = restTemplate.getForObject("http://localhost:8080/api/users/" + id, String.class);
        
        log.info("Order-Service: User-Check erfolgreich: {}", user);
        return "Bestellung für " + user + " wurde erstellt!";
    }
}