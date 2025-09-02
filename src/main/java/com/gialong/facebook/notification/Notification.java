package com.gialong.facebook.notification;

import com.gialong.facebook.base.BaseEntity;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "notifications",
    uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"target_id", "action_type", "recipient_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    /**
     * The entity (post or user) that the action was performed on. Example: when a user comments on a post,
     * the targetId will be the id of the post.
     */
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    private ActionEnum actionType;

    /**
     * The id of the action that was performed. Example: when a user comments on a post,
     * the actionPerformedId will be the id of the comment.
     */
    private UUID actionPerformedId;

    @Enumerated(EnumType.STRING)
    private StateEnum state = StateEnum.UNSEEN;

    private String redirectURL;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private User actor;  //The latest user who performed the action.

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;  //The user who is supposed to receive the notification.

}
