package com.gialong.facebook.userphoto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/photos")
@RequiredArgsConstructor
public class UserPhotoController {

    private final UserPhotoService photoService;

    @GetMapping("/{userId}")
    public List<UserPhotoResponse> getUserPhotos(@PathVariable UUID userId) {
        return photoService.getAllPhotosByUser(userId);
    }

    @DeleteMapping("/{id}")
    public void deletePhoto(@PathVariable UUID id) {
        photoService.deletePhoto(id);
    }
}