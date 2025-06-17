package com.example.echobackend.repository;

import com.example.echobackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Use Optional for methods that might not find a result

@Repository // Marks this interface as a Spring Data JPA repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query method to find a user by username (used in your Node.js auth controller)
    Optional<User> findByUsername(String username);

    // Optional: If you need to find by email during registration or login
    Optional<User> findByEmail(String email);
}