package com.gialong.facebook.userprofile;

import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {
    public UserProfileResponse toUserProfileResponse(final UserProfile userProfile) {
        return UserProfileResponse.builder()
                .userId(userProfile.getUserId())
                .username(userProfile.getUsername())
                .fullName(userProfile.getFullName())
                .avatarUrl(userProfile.getAvatarUrl())
                .coverUrl(userProfile.getCoverUrl())
                .bio(userProfile.getBio())
                .gender(userProfile.getGender() != null ? userProfile.getGender().name() : null)
                .dob(userProfile.getDob())
                .location(userProfile.getLocation())
                .website(userProfile.getWebsite())
                .build();
    }

}
