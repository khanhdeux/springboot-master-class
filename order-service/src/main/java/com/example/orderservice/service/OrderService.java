package com.example.orderservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final RestTemplate restTemplate;
    private final String USER_SERVICE_URL = "http://user-service:8080/api/users/";

    public OrderService(RestTemplateBuilder builder) {
        // Der builder ist entscheidend für das Jaeger-Tracing!
        this.restTemplate = builder.build();
    }

    /**
     * Legacy Methode ohne Schutz für den Vergleich in Jaeger.
     */
    public String checkUserLegacy(String id) {
        log.info("Order-Service (Legacy): Rufe User-Service auf für ID {}", id);
        return restTemplate.getForObject(USER_SERVICE_URL + id, String.class);
    }

    /**
     * Resiliente Methode mit Retry und Circuit Breaker.
     * Reihenfolge: Erst Retry, dann Circuit Breaker.
     */
    @Retry(name = "userServiceRetry")
    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "fallbackUserCheck")
    public String checkUserResilient(String id) {
        log.info("Order-Service (Resilient): Versuche User-Service Call für ID {}", id);
        return restTemplate.getForObject(USER_SERVICE_URL + id, String.class);
    }

    /**
     * Fallback Methode, wenn alle Retries scheitern oder der Breaker OPEN ist.
     */
    public String fallbackUserCheck(String id, Throwable t) {
        log.warn("Order-Service: Fallback für User {} aktiviert. Grund: {}", id, t.getMessage());
        return "Gast-User (Notbetrieb)";
    }
}