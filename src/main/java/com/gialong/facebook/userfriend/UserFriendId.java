package com.gialong.facebook.userfriend;

import com.gialong.facebook.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserFriendId implements Serializable {
    private User requester;
    private User addressee;
}
