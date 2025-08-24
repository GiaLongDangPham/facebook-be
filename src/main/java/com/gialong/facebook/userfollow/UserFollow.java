package com.gialong.facebook.userfollow;

import com.gialong.facebook.base.BaseEntity;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_follow")
@IdClass(UserFollowId.class)
@Data
public class UserFollow extends BaseEntity {
    /**
     * The target user that is being followed
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private User target;

    /**
     * The actor user that performs the following
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;
}
