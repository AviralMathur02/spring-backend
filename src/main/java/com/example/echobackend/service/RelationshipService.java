package com.example.echobackend.service;

import com.example.echobackend.model.Relationship;
import com.example.echobackend.model.User;
import com.example.echobackend.repository.RelationshipRepository;
import com.example.echobackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final UserRepository userRepository;

    public List<Long> getFollowerUserIdsForUser(Long followedUserId) {
        List<Relationship> relationships = relationshipRepository.findByFollowedUserId(followedUserId);
        return relationships.stream()
                .map(Relationship::getFollowerUserId)
                .collect(Collectors.toList());
    }

    public List<Long> getFollowedUserIds(Long followerUserId) {
        return relationshipRepository.findByFollowerUserId(followerUserId)
                .stream()
                .map(Relationship::getFollowedUserId)
                .collect(Collectors.toList());
    }

    @Transactional
    public String addRelationship(Long followerUserId, Long followedUserId) { // <<-- ADDED followerUserId parameter
        // The followerUserId is now passed directly from the controller
        // So, we no longer need to fetch it here.
        // The controller handles getting current user ID.

        if (followerUserId == null) { // Basic check for null followerId
            throw new RuntimeException("Follower ID cannot be null.");
        }

        if (followerUserId.equals(followedUserId)) {
            throw new RuntimeException("Cannot follow yourself!");
        }

        if (relationshipRepository.existsByFollowerUserIdAndFollowedUserId(followerUserId, followedUserId)) {
            return "Already following.";
        }

        Relationship newRelationship = new Relationship(followerUserId, followedUserId);
        relationshipRepository.save(newRelationship);
        return "Following";
    }

    @Transactional
    public String deleteRelationship(Long followerUserId, Long followedUserId) { // <<-- ADDED followerUserId parameter
        // The followerUserId is now passed directly from the controller
        // So, we no longer need to fetch it here.

        if (followerUserId == null) { // Basic check for null followerId
            throw new RuntimeException("Follower ID cannot be null.");
        }

        if (!relationshipRepository.existsByFollowerUserIdAndFollowedUserId(followerUserId, followedUserId)) {
            throw new RuntimeException("Not following this user.");
        }

        relationshipRepository.deleteByFollowerUserIdAndFollowedUserId(followerUserId, followedUserId);
        return "Unfollow";
    }

    public boolean isFollowing(Long currentUserId, Long followedUserId) { // <<-- ADDED currentUserId parameter
        // The currentUserId (authenticated user) is now passed directly from the controller
        // So, we no longer need to fetch it here.

        if (currentUserId == null) {
            return false; // Cannot check if not logged in or current user ID is unknown
        }
        return relationshipRepository.existsByFollowerUserIdAndFollowedUserId(currentUserId, followedUserId);
    }

    // --- EXISTING SERVICE METHODS (No changes needed here unless you want to refactor) ---

    /**
     * Gets the count of followers for a given user.
     * @param userId The ID of the user whose followers are to be counted.
     * @return The number of followers.
     */
    public long getFollowerCount(Long userId) {
        return relationshipRepository.countByFollowedUserId(userId);
    }

    /**
     * Gets the count of users followed by a given user.
     * @param userId The ID of the user whose following count is to be determined.
     * @return The number of users followed.
     */
    public long getFollowingCount(Long userId) {
        return relationshipRepository.countByFollowerUserId(userId);
    }

    /**
     * Gets a list of User objects who are following a given user.
     * @param userId The ID of the user whose followers are to be listed.
     * @return A list of User objects (followers).
     */
    public List<User> getFollowersList(Long userId) {
        return relationshipRepository.findFollowersOfUser(userId);
    }

    /**
     * Gets a list of User objects that a given user is following.
     * @param userId The ID of the user whose following list is to be retrieved.
     * @return A list of User objects (users being followed).
     */
    public List<User> getFollowingList(Long userId) {
        return relationshipRepository.findFollowingOfUser(userId);
    }
}