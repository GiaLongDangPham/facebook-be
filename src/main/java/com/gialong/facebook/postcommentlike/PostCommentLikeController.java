package com.gialong.facebook.postcommentlike;

import com.gialong.facebook.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/comments/likes")
@RequiredArgsConstructor
public class PostCommentLikeController {
    private final PostCommentLikeService commentLikeService;
    private final AuthService authService;

    @PostMapping("/{commentId}")
    public ResponseEntity<?> toggleLike(
            @PathVariable UUID commentId
    ) {
        UUID userId = authService.getMyInfo();
        boolean isLiked = commentLikeService.toggleLike(commentId, userId);
        return ResponseEntity.ok(Map.of(
                "isLiked", isLiked
        ));
    }

    @GetMapping("/{commentId}/is-liked")
    public ResponseEntity<?> isLiked(@PathVariable UUID commentId) {
        UUID userId = authService.getMyInfo();
        boolean isLiked = commentLikeService.isLiked(commentId, userId);
        return ResponseEntity.ok(Map.of(
                "isLiked", isLiked
        ));
    }

    @GetMapping("/{commentId}/total-likes")
    public ResponseEntity<?> countLikes(@PathVariable UUID commentId) {
        long totalLikes = commentLikeService.countLikes(commentId);
        return ResponseEntity.ok(Map.of(
                "totalLikes", totalLikes
        ));
    }
}