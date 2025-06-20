package com.example.echobackend.dto;

import lombok.Data; // Lombok for getters/setters
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String name;
}