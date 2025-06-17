package com.example.echobackend.service;

import com.example.echobackend.dto.RelationshipRequest;
import com.example.echobackend.model.Relationship;
import com.example.echobackend.model.User;
import com.example.echobackend.repository.RelationshipRepository;
import com.example.echobackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For delete operation

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final UserRepository userRepository; // To get authenticated user ID

    // Equivalent to Node.js getRelationships
    public List<Long> getFollowersOfUser(Long followedUserId) {
        List<Relationship> relationships = relationshipRepository.findByFollowerUserId(followedUserId);
        return relationships.stream()
                .map(Relationship::getFollowerUserId)
                .collect(Collectors.toList());
    }
    
    // Helper method for PostService to get IDs of users CURRENT_USER follows
    public List<Long> getFollowedUserIds(Long followerUserId) {
        return relationshipRepository.findByFollowerUserId(followerUserId)
                .stream()
                .map(Relationship::getFollowedUserId)
                .collect(Collectors.toList());
    }


    // Equivalent to Node.js addRelationship
    public String addRelationship(RelationshipRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Not logged in!");
        }

        String authenticatedUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
        Long followerUserId = currentUser.getId();
        Long followedUserId = request.getUserId(); // The user to follow

        if (followerUserId.equals(followedUserId)) {
            throw new RuntimeException("Cannot follow yourself!");
        }

        // Check if relationship already exists
        if (relationshipRepository.findByFollowerUserIdAndFollowedUserId(followerUserId, followedUserId).isPresent()) {
            return "Already following."; // Or throw an error if you prefer
        }

        Relationship newRelationship = new Relationship(followerUserId, followedUserId);
        relationshipRepository.save(newRelationship);
        return "Following";
    }

    // Equivalent to Node.js deleteRelationship
    @Transactional // Required for delete operations that use custom methods
    public String deleteRelationship(Long userIdToDelete) { // userIdToDelete is the followedUserId
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Not logged in!");
        }

        String authenticatedUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
        Long followerUserId = currentUser.getId(); // The current user unfollowing

        // Check if the relationship exists before deleting
        if (!relationshipRepository.findByFollowerUserIdAndFollowedUserId(followerUserId, userIdToDelete).isPresent()) {
            throw new RuntimeException("Not following this user.");
        }

        relationshipRepository.deleteByFollowerUserIdAndFollowedUserId(followerUserId, userIdToDelete);
        return "Unfollow";
    }
}