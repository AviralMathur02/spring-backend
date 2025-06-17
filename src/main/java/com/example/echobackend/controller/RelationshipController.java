package com.example.echobackend.controller;

import com.example.echobackend.dto.RelationshipRequest;
import com.example.echobackend.service.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relationships") // Maps to /api/relationships
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    @GetMapping
    public ResponseEntity<?> getRelationships(@RequestParam Long followedUserId) {
        try {
            // This endpoint gets FOLLOWERS of the given followedUserId
            List<Long> followerUserIds = relationshipService.getFollowersOfUser(followedUserId);
            return ResponseEntity.ok(followerUserIds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching relationships: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> addRelationship(@RequestBody RelationshipRequest request) {
        try {
            String message = relationshipService.addRelationship(request);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // e.g., "Not logged in!", "Cannot follow yourself!"
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding relationship.");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteRelationship(@RequestParam Long userId) { // userId here is the followedUserId to unfollow
        try {
            String message = relationshipService.deleteRelationship(userId);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // e.g., "Not logged in!", "Not following this user."
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting relationship.");
        }
    }
}