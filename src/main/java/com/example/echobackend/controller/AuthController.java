package com.example.echobackend.controller;

import com.example.echobackend.dto.LoginRequest;
import com.example.echobackend.dto.RegisterRequest;
import com.example.echobackend.dto.UserResponse; // NEW: Import UserResponse
import com.example.echobackend.model.User;
import com.example.echobackend.security.JwtService;
import com.example.echobackend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            String message = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            User user = authService.login(request); // This authenticates and returns the user object

            // Generate JWT Token using userId and username for the cookie
            String jwtToken = jwtService.generateToken(user.getId(), user.getUsername());

            // Set accessToken as HTTP-only cookie, similar to Node.js
            Cookie cookie = new Cookie("accessToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            // In production, uncomment these for security:
            // cookie.setSecure(true); // Only send over HTTPS
            // cookie.setSameSite("None"); // Crucial for cross-site requests (frontend on different port/domain)
            response.addCookie(cookie);

            // Create UserResponse object from the User entity, excluding sensitive fields
            

            return ResponseEntity.ok(jwtToken); // Return 200 OK with the DTO
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong password or username!");
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // Clear accessToken cookie
        Cookie cookie = new Cookie("accessToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Immediately expire the cookie
        // In production, uncomment these:
        // cookie.setSecure(true);
        // cookie.setSameSite("None");
        response.addCookie(cookie);
        return ResponseEntity.ok("User has been logged out.");
    }
}