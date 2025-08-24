package com.gialong.facebook.user;

import com.gialong.facebook.userprofile.UserProfile;
import com.gialong.facebook.userprofile.UserProfileMapper;
import com.gialong.facebook.userprofile.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final UserProfileMapper userProfileMapper;

    public UserResponse toUserResponse(final User user) {
        UserProfile userProfile = user.getProfile();
        UserProfileResponse profileResponse = userProfileMapper.toUserProfileResponse(userProfile);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .role(user.getRole().name())
                .profile(profileResponse)
                .build();
    }

}
