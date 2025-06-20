// server/src/main/java/com/example/echobackend/controller/RelationshipController.java
package com.example.echobackend.controller;

import com.example.echobackend.model.User;
import com.example.echobackend.service.RelationshipService;
import com.example.echobackend.repository.UserRepository; // <<-- NEW IMPORT
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.echobackend.dto.UserDTO;
import com.example.echobackend.dto.RelationshipRequest;

import java.util.List;

@RestController
@RequestMapping("/api/relationships")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;
    private final UserRepository userRepository; // <<-- INJECT UserRepository HERE

    // This endpoint should return the relationship status for the current user
    // The frontend currently expects this at `/api/relationships?followedUserId={userId}`
    @GetMapping
    public ResponseEntity<Boolean> checkFollowing(@RequestParam Long followedUserId) {
        try {
            Long currentUserId = getCurrentAuthenticatedUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }
            boolean isFollowing = relationshipService.isFollowing(currentUserId, followedUserId);
            return ResponseEntity.ok(isFollowing);
        } catch (RuntimeException e) {
            System.err.println("Error checking relationship: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        } catch (Exception e) {
            System.err.println("Unexpected error checking relationship: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping
    public ResponseEntity<String> addRelationship(@RequestBody RelationshipRequest request) {
        try {
            Long followerUserId = getCurrentAuthenticatedUserId();
            if (followerUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
            }
            String message = relationshipService.addRelationship(followerUserId, request.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
        catch (RuntimeException e) {
            System.err.println("Error adding relationship: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            System.err.println("Unexpected error adding relationship: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding relationship.");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteRelationship(@RequestBody RelationshipRequest request) {
        try {
            Long followerUserId = getCurrentAuthenticatedUserId();
            if (followerUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
            }
            String message = relationshipService.deleteRelationship(followerUserId, request.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (RuntimeException e) {
            System.err.println("Error deleting relationship: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error deleting relationship: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting relationship.");
        }
    }

    // --- MODIFIED HELPER METHOD ---
    private Long getCurrentAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            System.out.println("getCurrentAuthenticatedUserId: No authenticated user or is anonymousUser.");
            return null;
        }

        String username = authentication.getName(); // This will give the username
        System.out.println("getCurrentAuthenticatedUserId: Authenticated username: " + username);

        // Fetch the User entity from the database using the username
        return userRepository.findByUsername(username)
                .map(User::getId) // Map the Optional<User> to Optional<Long> (User's ID)
                .orElseGet(() -> { // Use orElseGet to provide a default if user is not found (shouldn't happen)
                    System.err.println("getCurrentAuthenticatedUserId: User not found in repository for username: " + username);
                    return null;
                });
    }

    // Existing count and list endpoints (no changes needed for logic, just ensuring they exist)
    @GetMapping("/followers/count")
    public ResponseEntity<Long> getFollowerCount(@RequestParam Long userId) {
        try {
            long count = relationshipService.getFollowerCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.err.println("Error getting follower count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }

    @GetMapping("/following/count")
    public ResponseEntity<Long> getFollowingCount(@RequestParam Long userId) {
        try {
            long count = relationshipService.getFollowingCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.err.println("Error getting following count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }

    @GetMapping("/followers/list")
    public ResponseEntity<List<User>> getFollowersList(@RequestParam Long userId) {
        try {
            List<User> followers = relationshipService.getFollowersList(userId);
            return ResponseEntity.ok(followers);
        } catch (Exception e) {
            System.err.println("Error getting followers list: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/following/list")
    public ResponseEntity<List<User>> getFollowingList(@RequestParam Long userId) {
        try {
            List<User> following = relationshipService.getFollowingList(userId);
            return ResponseEntity.ok(following);
        } catch (Exception e) {
            System.err.println("Error getting following list: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}