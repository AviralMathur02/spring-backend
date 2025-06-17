package com.example.echobackend.service;

import com.example.echobackend.dto.UserUpdateRequest;
import com.example.echobackend.model.User;
import com.example.echobackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication; // Added import
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public String updateUser(UserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated!");
        }

        String authenticatedUsername = authentication.getName(); // This is the username from the JWT

        User userToUpdate = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));

        // Update fields only if they are provided in the request
        if (request.getName() != null) {
            userToUpdate.setName(request.getName());
        }
        if (request.getCity() != null) {
            userToUpdate.setCity(request.getCity());
        }
        if (request.getWebsite() != null) {
            userToUpdate.setWebsite(request.getWebsite());
        }
        if (request.getProfilePic() != null) {
            userToUpdate.setProfilePic(request.getProfilePic());
        }
        if (request.getCoverPic() != null) {
            userToUpdate.setCoverPic(request.getCoverPic());
        }

        userRepository.save(userToUpdate);

        return "User has been updated.";
    }
}