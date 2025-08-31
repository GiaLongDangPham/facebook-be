package com.gialong.facebook.post;


import com.gialong.facebook.auth.AuthService;
import com.gialong.facebook.base.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request
    ) {
        UUID userId = authService.getMyInfo();
        return ResponseEntity.ok(postService.createPost(userId, request));
    }

    @GetMapping("/user/{currentUsername}")
    public ResponseEntity<PageResponse<PostResponse>> getPostsByUser(
            @PathVariable String currentUsername,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getPostsByUser(currentUsername, page, size));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID currentUserId = authService.getMyInfo();
        return ResponseEntity.ok(postService.getAllPosts(currentUserId, page, size));
    }

    @PutMapping("/privacy/{id}")
    public ResponseEntity<PostResponse> updatePrivacyPost(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request
    ) {
        String privacy = request.get("privacy");
        return ResponseEntity.ok(postService.updatePrivacyPost(id, privacy));
    }

    @PutMapping("/comment/{id}")
    public ResponseEntity<PostResponse> updateCommentBlockPost(
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> request
    ) {
        Boolean commentLocked = request.get("commentLocked");
        return ResponseEntity.ok(postService.updateCommentBlockPost(id, commentLocked));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}