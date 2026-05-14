package com.example.userservice.controller;

import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
// @Slf4j // Logging via Lombok
@Tag(name = "User Management", description = "Endpoints für die Verwaltung von Benutzern") //OpenAPI
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Beispiel für Strategie C: Media Type Versionierung
     * Nur wenn der Header 'Accept: application/vnd.company.app-v2+json' gesetzt ist,
     * wird diese Methode aufgerufen.
     */
    @GetMapping(value = "/info/version", produces = "application/vnd.company.app-v2+json")
    public String getVersionV2() {
        return "v2.0 (Enhanced)";
    }

    // Pfad: /api/users/info/version
    @Operation(summary = "Gibt die aktuelle API-Version zurück")
    @GetMapping("/info/version")
    public String getVersion() {
        log.info("Versionsabfrage aufgerufen");
        return "v2";
    }

    // GET ALL
    // @Operation(summary = "Alle Benutzer abrufen")
    // @GetMapping(produces = "application/json") // Präzisierung des Rückgabeformats
    // public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
    //     return ResponseEntity.ok(userService.findAllUsers());
    // }

    @Operation(summary = "Alle Benutzer mit Pagination")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(userService.findAllUsersPaginated(page, size));
    }  

    // GET BY ID
    @Operation(summary = "Einen Benutzer per ID finden")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("Suche Benutzer mit ID: {}", id);
        return ResponseEntity.ok(userService.findById(id));
    }

    // POST (CREATE)
    @Operation(summary = "Neuen Benutzer erstellen")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        log.info("Erstelle neuen Benutzer: {}", dto.getEmail());
        // HTTP 201 Created ist für POST zwingend für Senior-Niveau
        return new ResponseEntity<>(userService.save(dto), HttpStatus.CREATED);
    }

    // Bulk Operations. Netzweck Roundtrip zu sparen
    @Operation(summary = "Bulk-Erstellung von Benutzern")
    @PostMapping("/bulk")
    public ResponseEntity<List<UserResponseDTO>> createUsers(@RequestBody List<@Valid UserRequestDTO> dtos) {
        log.info("Bulk-Erstellung von {} Benutzern gestartet", dtos.size());
        // Annahme: Service hat eine saveAll Methode
        return new ResponseEntity<>(userService.saveAll(dtos), HttpStatus.CREATED);
    }    

    // PUT (FULL UPDATE)
    @Operation(summary = "Benutzer vollständig aktualisieren")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {
        log.info("Update (PUT) für Benutzer ID: {}", id);
        return ResponseEntity.ok(userService.update(id, dto));
    }

    // PATCH (PARTIAL UPDATE)
    @Operation(summary = "Benutzer teilweise aktualisieren")
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<UserResponseDTO> patchUser(@PathVariable Long id, @RequestBody UserRequestDTO dto) {
        log.info("Teil-Update für ID: {}", id);
        // Keine @Valid Prüfung hier, um Teil-Updates zu erlauben (MapStruct ignoriert nulls)
        return ResponseEntity.ok(userService.update(id, dto));
    }

    // DELETE
    @Operation(summary = "Benutzer löschen")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        // HTTP 204 No Content ist Best Practice für DELETE ohne Body
        return ResponseEntity.noContent().build();
    }
}