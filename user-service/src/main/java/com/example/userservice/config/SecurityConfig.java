package com.example.userservice.config;

import com.example.userservice.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // CSRF deaktivieren — nicht nötig bei JWT (kein Browser-Cookie)
            .csrf(csrf -> csrf.disable())

            // STATELESS — kein Session-Speicher, jeder Request braucht Token
            .sessionManagement(s ->
                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Regeln: wer darf was?
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()              // Login/Register = public
                .requestMatchers("/actuator/**").permitAll()          // Health Check = public
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("GET", "/api/v1/users").hasRole("ADMIN") // nur ADMIN
                .anyRequest().authenticated()                         // alles andere = eingeloggt
            )

            // Unser JwtAuthFilter VOR dem Standard-Filter ausführen
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    // BCrypt = sicherer Passwort-Hasher (Standard in Industrie)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager — Spring braucht ihn für Login-Logik
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}