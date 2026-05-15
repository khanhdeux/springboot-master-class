package com.example.userservice.service;

import com.example.userservice.dto.AuthResponseDTO;
import com.example.userservice.dto.LoginRequestDTO;
import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDTO register(LoginRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getUsername() + "@example.com");
        // Passwort verschlüsseln — NIEMALS Plaintext speichern!
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponseDTO(token, user.getRole().name());
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        // Spring prüft automatisch: username existiert? Passwort stimmt?
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.getUsername(), dto.getPassword())
        );

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponseDTO(token, user.getRole().name());
    }
}