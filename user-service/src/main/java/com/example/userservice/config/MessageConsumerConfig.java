package com.example.userservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Consumer;

@Configuration
public class MessageConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumerConfig.class);

    /**
     * Consumer für RabbitMQ.
     * Der Methodenname "orderSignal" muss exakt mit dem Eintrag in der 
     * application.yml unter spring.cloud.function.definition übereinstimmen.
     */
    @Bean
    public Consumer<String> orderSignal() {
        return message -> {
            log.info(">>>> [RECEIVER - RABBITMQ]");
            log.info("User-Service hat Signal empfangen: {}", message);
            
            // Logik-Check für deine Trading-Signale
            if (message.contains("tb")) {
                log.info("Aktion: Verarbeite Trendline Break (tb) Validierung...");
            } else if (message.contains("tr")) {
                log.info("Aktion: Verarbeite Trendline Reverse (tr) Validierung...");
            } else if (message.contains("fr")) {
                log.warn("Warnung: False Reversed (fr) erkannt - prüfe auf vorzeitiges Signal (premature)!");
            }
            
            log.info(">>>> [VERARBEITUNG ABGESCHLOSSEN]");
        };
    }

    /**
     * Consumer für Kafka.
     * Wird für die Langzeitanalyse deiner Trading-Signale genutzt.
     */
    @Bean
    public Consumer<String> tradingAnalytics() {
        return message -> {
            log.info(">>>> [RECEIVER - KAFKA]");
            log.info("User-Service (Analytics): Speichere Event für Statistik -> {}", message);
            // In einer echten App würde das hier in eine Zeitreihen-Datenbank fließen
        };
    }
}