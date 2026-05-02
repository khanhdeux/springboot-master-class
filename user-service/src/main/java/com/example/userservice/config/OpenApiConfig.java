package com.example.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Ersetze diese URL durch deine tatsächliche Codespace-URL (ohne das /api/users
        // am Ende)
        String codespaceUrl = "https://scaling-journey-pwgwvr697qc697p-8080.app.github.dev";

        return new OpenAPI()
                .servers(List.of(
                        new Server().url(codespaceUrl).description("Codespace Environment"),
                        new Server().url("http://localhost:8080").description("Local Development")));
    }
}