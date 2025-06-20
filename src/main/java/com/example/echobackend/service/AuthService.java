package com.example.echobackend.service;

import com.example.echobackend.dto.LoginRequest;
import com.example.echobackend.dto.RegisterRequest;
import com.example.echobackend.model.User;
import com.example.echobackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// IMPORTANT: This class MUST NOT 'implement UserDetailsService'
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User with this username already exists!");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setName(request.getName());
        // Set other default fields if necessary
        newUser.setCoverPic("defaultCover.jpg"); // Example default
        newUser.setProfilePic("defaultProfile.jpg"); // Example default
        newUser.setCity("Unknown");
        // newUser.setWebsite("N/A"); // <--- REMOVE THIS LINE (The old line 40)

        // --- NEW: Set default values for websiteName and websiteUrl ---
        newUser.setWebsiteName("N/A"); // Default website name
        newUser.setWebsiteUrl(""); // Default empty website URL

        userRepository.save(newUser);
        return "User registered successfully!";
    }

    public User login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            return userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found after successful authentication."));
        } catch (AuthenticationException e) {
            throw e;
        }
    }
}