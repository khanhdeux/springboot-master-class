package com.example.userservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;

    // 1. Standard-Konstruktor (Pflicht für JPA)
    public User() {}

    // 2. Komfort-Konstruktor (Für DataInitializer)
    public User(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // 3. Getter (Pflicht für JSON/Jackson)
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    // 4. Setter (Best Practice)
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
}