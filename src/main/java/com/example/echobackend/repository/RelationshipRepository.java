package com.example.echobackend.repository;

import com.example.echobackend.model.Relationship;
// No direct import needed for Relationship.RelationshipId if it's referenced as an inner class
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Relationship.RelationshipId> { // IMPORTANT: Reference the nested class correctly

    // Custom method to find all followed user IDs by a followerUserId
    List<Relationship> findByFollowerUserId(Long followerUserId);

    // Custom method to find if a relationship exists
    Optional<Relationship> findByFollowerUserIdAndFollowedUserId(Long followerUserId, Long followedUserId);

    // Custom method to delete a relationship
    void deleteByFollowerUserIdAndFollowedUserId(Long followerUserId, Long followedUserId);
}