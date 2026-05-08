package com.example.orderservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageProducer {
    private static final Logger log = LoggerFactory.getLogger(OrderMessageProducer.class);
    private final StreamBridge streamBridge;

    public OrderMessageProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    // Sendet an RabbitMQ (für schnelle Order-Befehle)
    public void sendToRabbit(String orderId, String signalType) {
        log.info("Messaging (RabbitMQ): Sende Signal {} für Order {}", signalType, orderId);
        streamBridge.send("orderSignal-out-0", "RabbitMQ-Event: " + orderId + " [" + signalType + "]");
    }

    // Sendet an Kafka (für Analyse der Signale tb, tr, fr)
    public void sendToKafka(String orderId, String signalType) {
        log.info("Messaging (Kafka): Sende Analyse-Daten für Signal {} (Order {})", signalType, orderId);
        streamBridge.send("tradingAnalytics-out-0", "Kafka-Log: " + orderId + " | Type: " + signalType);
    }
}