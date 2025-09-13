package com.gialong.facebook.user;

import com.gialong.facebook.userprofile.UserProfileResponse;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String status;
    private String role;
    private UserProfileResponse profile;
    private Long lastOffline; // timestamp lần offline cuối
}
