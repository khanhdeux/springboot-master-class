package com.example.userservice.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// @Component = Spring verwaltet diese Klasse
// Verantwortlich für: Token erstellen, lesen, validieren
@Component
public class JwtUtil {

    // Wert kommt aus application.yml: jwt.secret
    @Value("${jwt.secret}")
    private String secret;

    // Wert kommt aus application.yml: jwt.expiration
    @Value("${jwt.expiration}")
    private long expiration;

    // Erstellt den kryptografischen Schlüssel aus dem secret
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Token erstellen — wird nach Login aufgerufen
    // Enthält: username (subject), rolle (claim), Ablaufzeit
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)           // wer ist eingeloggt
                .claim("role", role)         // welche Rolle hat er
                .issuedAt(new Date())        // wann erstellt
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())          // digital signiert
                .compact();
    }

    // Username aus Token lesen
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Prüfen ob Token gültig ist (nicht abgelaufen, nicht manipuliert)
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false; // Token ungültig oder abgelaufen
        }
    }
}