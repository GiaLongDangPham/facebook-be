package com.gialong.facebook.userfriend;

import com.gialong.facebook.base.BaseEntity;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_friends")
@IdClass(UserFriendId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFriend extends BaseEntity {
    // Người gửi lời mời
    @Id
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    // Người nhận lời mời
    @Id
    @ManyToOne
    @JoinColumn(name = "addressee_id", nullable = false)
    private User addressee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;
}
