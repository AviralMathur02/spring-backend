package com.example.echobackend.repository;

import com.example.echobackend.model.Relationship;
import com.example.echobackend.model.Relationship.RelationshipId;
import com.example.echobackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, RelationshipId> {

    List<Relationship> findByFollowerUserId(Long followerUserId);

    Optional<Relationship> findByFollowerUserIdAndFollowedUserId(Long followerUserId, Long followedUserId);

    boolean existsByFollowerUserIdAndFollowedUserId(Long followerUserId, Long followedUserId);

    void deleteByFollowerUserIdAndFollowedUserId(Long followerUserId, Long followedUserId);

    List<Relationship> findByFollowedUserId(Long followedUserId);

    long countByFollowedUserId(Long followedUserId);

    long countByFollowerUserId(Long followerUserId);

    // THIS IS THE CORRECTED QUERY: SELECTS the User entity 'u' directly
    @Query("SELECT u FROM User u JOIN Relationship r ON u.id = r.followerUserId WHERE r.followedUserId = :followedUserId")
    List<User> findFollowersOfUser(@Param("followedUserId") Long followedUserId);

    // THIS IS THE CORRECTED QUERY: SELECTS the User entity 'u' directly
    @Query("SELECT u FROM User u JOIN Relationship r ON u.id = r.followedUserId WHERE r.followerUserId = :followerUserId")
    List<User> findFollowingOfUser(@Param("followerUserId") Long followerUserId);
}
