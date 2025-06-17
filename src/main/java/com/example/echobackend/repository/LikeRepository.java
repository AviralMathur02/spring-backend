package com.example.echobackend.repository;

import com.example.echobackend.model.Like;
// No direct import needed for Like.LikeId if it's referenced as an inner class
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Like.LikeId> { // IMPORTANT: Reference the nested class correctly

    List<Like> findByPostId(Long postId);

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId); // This method needs @Transactional in the service
}