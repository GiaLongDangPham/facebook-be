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
    private final UserService userService;

    public void sendLikePostNotification(UUID recipientId, UUID senderId, UUID postId) {
        Optional<Notification> existing = notificationRepository
                .findByTargetIdAndActionType(postId, ActionEnum.LIKE_POST);
        User recipient = userService.getUserById(recipientId);
        User actor = userService.getUserById(senderId);
        Notification notification;
        if (existing.isPresent()) {
            notification = existing.get();
            notification.setState(StateEnum.UNSEEN);
            notification.setActor(actor);
            notification.setUpdatedAt(Instant.now());
        } else {
            notification = Notification.builder()
                    .targetId(postId)
                    .actionType(ActionEnum.LIKE_POST)
                    .state(StateEnum.UNSEEN)
                    .redirectURL("/user/posts/" + postId)
                    .recipient(recipient)
                    .actor(actor)
                    .build();
        }
        NotificationResponse response = notificationMapper.toResponse(notificationRepository.save(notification));
        // convertAndSend đến user nhận
        messagingTemplate.convertAndSend("/topic/notification/" + recipientId, response);
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
    public void updateNotificationState (UUID targetId, ActionEnum actionType, StateEnum newState) {
        notificationRepository.updateNotificationState(targetId, actionType, newState);
    }

    @Transactional
    public void markAllSeen(UUID userId) {
        notificationRepository.markAllSeen(userId, StateEnum.SEEN, StateEnum.UNSEEN);
    }
}