package com.example.userservice.controller;

import com.example.userservice.dto.AuthResponseDTO;
import com.example.userservice.dto.LoginRequestDTO;
import com.example.userservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Neuen User registrieren → gibt sofort Token zurück
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    // Einloggen → Token erhalten
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}