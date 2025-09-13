package com.gialong.facebook.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gialong.facebook.base.BaseRedisService;
import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.userprofile.UserProfile;
import com.gialong.facebook.userprofile.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final BaseRedisService redisService;
    private final ObjectMapper objectMapper;

    @NonFinal
    private static final String ONLINE_USERS_KEY = "ONLINE_USERS";

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserResponse).toList();
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public User getUserByUsername(String username) {
        UserProfile profile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return profile.getUser();
    }

    public void setUserOnline(UserResponse user) {
        // Lưu cả object vào Redis (RedisTemplate sẽ serialize)
        redisService.hashSet(ONLINE_USERS_KEY, user.getId().toString(), user);
    }

    public void setUserOffline(UUID userId) {
        redisService.delete(ONLINE_USERS_KEY, userId.toString());

        // Lưu thời điểm offline
        long now = System.currentTimeMillis();
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastOffline(now);
            userRepository.save(user);
        });
    }

    public List<UserResponse> getOnlineUsers() {
        return redisService.getField(ONLINE_USERS_KEY)
                .values()
                .stream()
                .map(o -> objectMapper.convertValue(o, UserResponse.class)) // cast lại
                .toList();
    }
}
