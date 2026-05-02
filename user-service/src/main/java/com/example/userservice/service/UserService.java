package com.example.userservice.service;

import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Holt alle User und mappt sie auf Response-DTOs.
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAllUsers() {
        log.info("Fetching all users from database");
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Findet einen User per ID oder wirft eine Exception.
     */
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User mit ID " + id + " nicht gefunden"));
        return userMapper.toResponseDto(user);
    }

    /**
     * Erstellt einen neuen User.
     */
    @Transactional
    public UserResponseDTO save(UserRequestDTO dto) {
        log.info("Saving new user: {}", dto.getUsername());
        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    /**
     * Aktualisiert einen User (Full PUT oder Partial PATCH).
     * MapStruct ignoriert null-Werte im DTO.
     */
    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        log.info("Updating user with id: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User mit ID " + id + " nicht gefunden"));

        // Überträgt nur nicht-leere Felder vom DTO auf die Entity
        userMapper.updateEntityFromDto(dto, existingUser);

        // Dank @Transactional und Dirty Checking wird die Änderung 
        // automatisch beim Commit in die DB geschrieben. 
        // userRepository.save(existingUser) ist optional, aber oft für Klarheit beibehalten.
        return userMapper.toResponseDto(existingUser);
    }

    /**
     * Löscht einen User.
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Löschen fehlgeschlagen: User " + id + " existiert nicht");
        }
        userRepository.deleteById(id);
    }
}