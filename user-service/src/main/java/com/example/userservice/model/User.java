package com.example.userservice.model;

import jakarta.persistence.*;

// Spring Security Interface — zwingt uns, Methoden zu implementieren
// die Spring Security braucht um den User zu kennen
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
// implements UserDetails = dieser User ist Spring-Security-kompatibel
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    // NEU: Passwort (verschlüsselt gespeichert)
    private String password;

    // NEU: Rolle — gespeichert als String in DB (z.B. "USER" oder "ADMIN")
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    // Konstruktoren
    public User() {}

    public User(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = Role.USER;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }

    // ─── UserDetails Interface — PFLICHT ──────────────────────────────
    // Spring Security ruft diese Methoden auf um den User zu verstehen

    // Gibt die Rollen zurück — "ROLE_ADMIN" oder "ROLE_USER"
    // Spring erwartet das Prefix "ROLE_" — deshalb + role.name()
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    // Diese 4 Methoden müssen true zurückgeben damit User einloggen kann
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}