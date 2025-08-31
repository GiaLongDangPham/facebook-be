package com.gialong.facebook.userprofile;

import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.user.UserGender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfileResponse getProfile(String username) {
        return userProfileRepository.findByUsername(username)
                .map(userProfileMapper::toUserProfileResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UserProfileRequest request) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        profile.setUsername(request.getUsername());
        profile.setFullName(request.getFullName());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setCoverUrl(request.getCoverUrl());
        profile.setBio(request.getBio());
        profile.setGender(request.getGender() != null ? UserGender.valueOf(request.getGender()) : profile.getGender());
        profile.setDob(request.getDob());
        profile.setLocation(request.getLocation());
        profile.setWebsite(request.getWebsite());

        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(profile));
    }
}