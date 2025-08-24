package com.gialong.facebook.userfollow;

import com.gialong.facebook.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserFollowId implements Serializable {
    private User actor;
    private User target;
}
