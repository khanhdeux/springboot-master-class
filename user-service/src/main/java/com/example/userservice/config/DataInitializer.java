package com.example.userservice.config;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            // Wir prüfen, ob schon Daten da sind, um Dopplungen zu vermeiden
            if (repository.count() == 0) {
                repository.save(new User(null, "Admin_Khanh", "admin@dev.de"));
                repository.save(new User(null, "Trader_Expert", "trader@finance.com"));
                System.out.println(">> Testdaten erfolgreich geladen!");
            }
        };
    }
}