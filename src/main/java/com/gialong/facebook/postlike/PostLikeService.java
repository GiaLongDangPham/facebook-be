package com.gialong.facebook.postlike;

import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.notification.NotificationService;
import com.gialong.facebook.post.Post;
import com.gialong.facebook.post.PostRepository;
import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public boolean toggleLike(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Optional<PostLike> existing = postLikeRepository.findByPostIdAndUserId(postId, userId);

        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get()); // unlike
            return false;
        } else {
            PostLike like = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(like); // like

            // gửi noti cho chủ post
            UUID recipientId = post.getAuthor().getId();
            notificationService.sendLikePostNotification(recipientId, userId, postId);
            return true;
        }
    }

    public Boolean isLiked(UUID postId, UUID userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    public long countLikes(UUID postId) {
        return postLikeRepository.countByPostId(postId);
    }
}
