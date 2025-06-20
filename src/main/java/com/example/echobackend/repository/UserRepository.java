package com.example.echobackend.repository;

import com.example.echobackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set; // Import Set for the new method

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(String usernameQuery, String nameQuery);

    // NEW: Method to find users whose IDs are NOT in a given set (for suggestions)
    // This will fetch users that the current user is not following and is not themselves.
    List<User> findAllByIdNotIn(Set<Long> userIdsToExclude);
    // You can also add pagination if you expect many users for suggestions:
    // Page<User> findAllByIdNotIn(Set<Long> userIdsToExclude, Pageable pageable);
}