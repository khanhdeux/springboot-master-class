package com.example.userservice.config;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.userservice.model.Role;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.count() == 0) {
                for (int i = 1; i <= 20; i++) {
                    User user = new User();
                    user.setUsername("User_" + i);
                    user.setEmail("user" + i + "@example.com");
                    user.setPassword(passwordEncoder.encode("password123"));
                    user.setRole(Role.USER);
                    repository.save(user);
                }
                // Admin auch erstellen
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                repository.save(admin);
            }
        };
    }
}