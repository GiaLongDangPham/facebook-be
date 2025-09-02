package com.gialong.facebook.notification;

import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(User recipient, User sender, UUID postId, UUID commentId, ActionEnum actionEnum) {
        Notification notification = Notification.builder()
                .actor(sender)
                .recipient(recipient)
                .state(StateEnum.UNSEEN)
                .build();
        switch (actionEnum) {
            case LIKE_POST -> {
                notification.setTargetId(postId);
                notification.setActionType(ActionEnum.LIKE_POST);
                notification.setRedirectURL("/user/posts/" + postId);
            }
            case COMMENT_POST -> {
                notification.setTargetId(postId);
                notification.setActionType(ActionEnum.COMMENT_POST);
                notification.setActionPerformedId(commentId);
                notification.setRedirectURL("/user/posts/" + postId);
            }
            case ADD_FRIEND -> {
                notification.setTargetId(sender.getId());
                notification.setActionType(ActionEnum.ADD_FRIEND);
                notification.setRedirectURL("/user/friends/requests");
            }
            case ACCEPT_FRIEND -> {
                notification.setTargetId(sender.getId());
                notification.setActionType(ActionEnum.ACCEPT_FRIEND);
                notification.setRedirectURL("/user/profile/" + sender.getProfile().getUsername());

                Notification addFriendNotification = notificationRepository
                        .findByTargetIdAndActionTypeAndRecipient(recipient.getId(), ActionEnum.ADD_FRIEND, sender.getId())
                        .orElseThrow();
                notificationRepository.delete(addFriendNotification);
            }
        }

        NotificationResponse response = notificationMapper.toResponse(notificationRepository.save(notification));
        // convertAndSend đến user nhận
        messagingTemplate.convertAndSend("/topic/notification/" + recipient.getId(), response);
    }

    public List<NotificationResponse> getNotifications(UUID recipientId) {
        return notificationRepository.findByRecipientIdAndCreatedAtAfterOrderByUpdatedAtDesc(
                        recipientId,
                        Instant.now().minus(30, ChronoUnit.DAYS))
                .stream().map(notificationMapper::toResponse).toList();
    }

    public Long countUnseen(UUID recipientId) {
        return notificationRepository.countUnseen(recipientId, StateEnum.UNSEEN);
    }

    @Transactional
    public void updateNotificationState (UUID targetId, ActionEnum actionType, UUID recipientId, StateEnum newState) {
        notificationRepository.updateNotificationState(targetId, actionType, recipientId, newState);
    }

    @Transactional
    public void markAllSeen(UUID userId) {
        notificationRepository.markAllSeen(userId, StateEnum.SEEN, StateEnum.UNSEEN);
    }

    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}