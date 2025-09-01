package com.gialong.facebook.notification;

import com.gialong.facebook.post.PostRepository;
import com.gialong.facebook.postcomment.PostCommentRepository;
import com.gialong.facebook.postlike.PostLikeRepository;
import com.gialong.facebook.user.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class NotificationMapper {

    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final PostLikeRepository likeRepository;
    private final UserMapper userMapper;

    @Transactional
    public NotificationResponse toResponse(final Notification notification) {
        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setTargetId(notification.getTargetId());
        notificationResponse.setActor(userMapper.toUserResponse(notification.getActor()));
        notificationResponse.setActionType(notification.getActionType());
        notificationResponse.setRedirectURL(notification.getRedirectURL());
        notificationResponse.setCreatedAt(notification.getCreatedAt());
        notificationResponse.setUpdatedAt(notification.getUpdatedAt());
        notificationResponse.setState(notification.getState());
        notificationResponse.setRecipientId(notification.getRecipient().getId());
        notificationResponse.setActionPerformedId(notification.getActionPerformedId());

        Long actorCount = null;
        switch (notification.getActionType()) {
            case LIKE_POST:
                actorCount = likeRepository.countByPostId(notification.getTargetId());
                notificationResponse.setPreviewText(postRepository.getContentById(notification.getTargetId()));
                break;
            case COMMENT_POST, COMMENT_REPLY:
                notificationResponse.setActionPerformedId(notification.getActionPerformedId());
                if(notification.getActionType() == ActionEnum.COMMENT_POST) {
                    actorCount = commentRepository.countDistinctUsersByPostId(notification.getTargetId());
                }
                break;
            default:
                break;
        }
        notificationResponse.setActorCount(actorCount);
        return notificationResponse;
    }
}
