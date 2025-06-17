package com.example.echobackend.controller;

import com.example.echobackend.dto.UserUpdateRequest;
import com.example.echobackend.model.User;
import com.example.echobackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Added import
import org.springframework.security.core.context.SecurityContextHolder; // Added import
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/find/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        Optional<User> userOptional = userService.getUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Exclude password before sending to client, similar to Node.js
            // Ensure the User constructor matches the fields you're passing.
            // If you added new fields to User, you need to include them here or adjust the constructor.
            User userWithoutPassword = new User(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                null, // password is set to null for security
                user.getName(),
                user.getCity(), // New field
                user.getWebsite(), // New field
                user.getProfilePic(), // New field
                user.getCoverPic() // New field
            );
            return ResponseEntity.ok(userWithoutPassword);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateRequest request) {
        try {
            String message = userService.updateUser(request);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during user update.");
        }
    }
}