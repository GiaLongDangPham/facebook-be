package com.gialong.facebook.notification;

import com.gialong.facebook.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final AuthService authService;

    @GetMapping("/{recipientId}")
    public List<NotificationResponse> getNotifications(@PathVariable UUID recipientId) {
        return notificationService.getNotifications(recipientId);
    }

    @GetMapping("/{recipientId}/count-unseen")
    public ResponseEntity<Map<String, Long>> getUnseenCount(@PathVariable UUID recipientId) {
        Long countUnseen = notificationService.countUnseen(recipientId);
        return ResponseEntity.ok(Map.of("countUnseen", countUnseen));
    }

    @PatchMapping
    public ResponseEntity<Void> updateNotificationState(
            @RequestParam UUID targetId,
            @RequestParam ActionEnum actionType,
            @RequestParam UUID recipientId,
            @RequestParam StateEnum newState
    ) {
        notificationService.updateNotificationState(targetId, actionType, recipientId, newState);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mark-all-seen")
    public ResponseEntity<Void> markAllSeen() {
        UUID userId = authService.getMyInfo();
        notificationService.markAllSeen(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable UUID notificationId) {
        notificationService.deleteNotification(notificationId);
    }
}
