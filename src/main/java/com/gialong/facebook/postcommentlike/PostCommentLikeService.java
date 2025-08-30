package com.gialong.facebook.postcommentlike;

import com.gialong.facebook.postcomment.PostComment;
import com.gialong.facebook.postcomment.PostCommentRepository;
import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostCommentLikeService {
    private final PostCommentLikeRepository commentLikeRepo;
    private final PostCommentRepository commentRepo;
    private final UserRepository userRepo;

    @Transactional
    public boolean toggleLike(UUID commentId, UUID userId) {
        PostComment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return commentLikeRepo.findByCommentAndUser(comment, user)
                .map(like -> {
                    commentLikeRepo.delete(like);
                    return false; // đã unlike
                })
                .orElseGet(() -> {
                    commentLikeRepo.save(PostCommentLike.builder()
                            .comment(comment)
                            .user(user)
                            .build());
                    return true; // đã like
                });
    }

    public boolean isLiked(UUID commentId, UUID userId) {
        return commentLikeRepo.existsByCommentIdAndUserId(commentId, userId);
    }

    public long countLikes(UUID commentId) {
        return commentLikeRepo.countByCommentId(commentId);
    }

}