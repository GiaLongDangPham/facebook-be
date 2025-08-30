package com.gialong.facebook.postlike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {
    Optional<PostLike> findByPostIdAndUserId(UUID postId, UUID userId);
    Boolean existsByPostIdAndUserId(UUID postId, UUID userId);
    long countByPostId(UUID postId);
}