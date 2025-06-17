package com.example.echobackend.repository;

import com.example.echobackend.model.Post;
import com.example.echobackend.model.User; // Make sure User is imported here
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Corrected method name to use the User's ID for a single user's posts
    List<Post> findByUser_IdOrderByCreatedAtDesc(Long userId);

    // Corrected method name to use a list of User IDs for posts from multiple users
    List<Post> findByUser_IdInOrderByCreatedAtDesc(List<Long> userIds);
}