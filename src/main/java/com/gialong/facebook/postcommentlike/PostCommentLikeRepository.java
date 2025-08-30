package com.gialong.facebook.postcommentlike;

import com.gialong.facebook.postcomment.PostComment;
import com.gialong.facebook.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, PkUserCommentLike> {
    Optional<PostCommentLike> findByCommentAndUser(PostComment comment, User user);

    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);

    long countByCommentId(UUID commentId);
}