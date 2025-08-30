package com.gialong.facebook.postcomment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, UUID> {
    Page<PostComment> findByPostIdAndParentIsNull(UUID postId, Pageable pageable);
    long countByPostId(UUID postId);
    long countByPostIdAndParentId(UUID postId, UUID parentId);

    Page<PostComment> findByParentId(UUID commentId, Pageable pageable);
}