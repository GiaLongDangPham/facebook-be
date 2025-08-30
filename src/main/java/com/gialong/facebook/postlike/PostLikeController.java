package com.gialong.facebook.postlike;

import com.gialong.facebook.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/posts/likes")
@RequiredArgsConstructor
public class PostLikeController {
    private final PostLikeService postLikeService;
    private final AuthService authService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> toggleLike(@PathVariable UUID postId) {
        UUID userId = authService.getMyInfo();
        boolean isLiked = postLikeService.toggleLike(postId, userId);
        return ResponseEntity.ok(Map.of(
                "isLiked", isLiked
        ));
    }

    @GetMapping("/{postId}/is-liked")
    public ResponseEntity<?> isLiked(@PathVariable UUID postId) {
        UUID userId = authService.getMyInfo();
        boolean isLiked = postLikeService.isLiked(postId, userId);
        return ResponseEntity.ok(Map.of(
                "isLiked", isLiked
        ));
    }

    @GetMapping("/{postId}/total-likes")
    public ResponseEntity<?> totalLikes(@PathVariable UUID postId) {
        long totalLikes = postLikeService.countLikes(postId);
        return ResponseEntity.ok(Map.of(
                "totalLikes", totalLikes
        ));
    }
}