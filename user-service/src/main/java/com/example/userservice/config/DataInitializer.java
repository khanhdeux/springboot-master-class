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
            if (repository.count() == 0) {
                for (int i = 1; i <= 20; i++) {
                    repository.save(new User(null,
                        "User_" + i,
                        "user" + i + "@example.com"));
                }
                System.out.println(">> 20 Testuser geladen!");
            }
        };
    }
}