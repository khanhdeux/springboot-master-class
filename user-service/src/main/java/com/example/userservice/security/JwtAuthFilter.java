package com.example.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Dieser Filter läuft bei JEDEM Request einmal
// Aufgabe: Token aus Header lesen → validieren → User in SecurityContext setzen
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 1. Header lesen: "Authorization: Bearer eyJhb..."
        String authHeader = request.getHeader("Authorization");

        // 2. Kein Token → weiter ohne Authentifizierung
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " entfernen → nur Token
        String token = authHeader.substring(7);

        // 4. Token validieren
        if (jwtUtil.isTokenValid(token)) {
            String username = jwtUtil.extractUsername(token);

            // 5. User aus DB laden
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 6. User in SecurityContext setzen
            // → Spring Security weiß jetzt wer eingeloggt ist
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 7. Request weitergeben
        chain.doFilter(request, response);
    }
}