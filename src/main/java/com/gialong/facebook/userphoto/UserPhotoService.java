package com.gialong.facebook.userphoto;

import com.gialong.facebook.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPhotoService {

    private final UserPhotoRepository userPhotoRepository;

    public List<UserPhotoResponse> getAllPhotosByUser(UUID userId) {
        return userPhotoRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(userPhoto -> UserPhotoResponse.builder()
                        .id(userPhoto.getId())
                        .userId(userPhoto.getUser().getId())
                        .url(userPhoto.getUrl())
                        .build())
                .toList();
    }

    public void savePhoto(User user, String url) {
        UserPhoto photo = UserPhoto.builder()
                .user(user)
                .url(url)
                .build();
        userPhotoRepository.save(photo);
    }

    public void deletePhoto(UUID id) {
        userPhotoRepository.deleteById(id);
    }
}
