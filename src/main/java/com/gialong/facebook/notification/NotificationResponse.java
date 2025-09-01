package com.gialong.facebook.notification;

import com.gialong.facebook.user.UserResponse;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse{
    private UUID id;
    private UUID targetId;
    private UserResponse actor;
    private UUID actionPerformedId;
    private ActionEnum actionType;
    private String redirectURL;
    private Instant createdAt;
    private Instant updatedAt;

    private StateEnum state = StateEnum.UNSEEN;
    private UUID recipientId;

    // thêm thông tin enrich
    private Long actorCount;    // số người đã like/comment
    private String previewText; // caption hoặc comment text
    private String imageURL;    // thumbnail post
}
