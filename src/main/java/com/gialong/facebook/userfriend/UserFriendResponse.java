package com.gialong.facebook.userfriend;

import com.gialong.facebook.user.UserResponse;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFriendResponse {
    private UserResponse otherUser;
    private String status;
}
