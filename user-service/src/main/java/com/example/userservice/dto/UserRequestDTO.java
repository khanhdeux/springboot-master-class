package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestDTO {

    @NotBlank(message = "Username darf nicht leer sein")
    @Size(min = 3, max = 20, message = "Username muss zwischen 3 und 20 Zeichen lang sein")
    private String username;

    @Email(message = "Ungültiges E-Mail Format")
    @NotBlank(message = "E-Mail darf nicht leer sein")
    private String email;

    // Standard-Konstruktoren, Getter und Setter (manuell)
    public UserRequestDTO() {}
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}