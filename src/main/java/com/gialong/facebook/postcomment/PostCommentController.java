package com.gialong.facebook.postcomment;

import com.gialong.facebook.auth.AuthService;
import com.gialong.facebook.base.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/posts")
@RequiredArgsConstructor
public class PostCommentController {
    private final PostCommentService commentService;
    private final AuthService authService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<PostCommentResponse> addComment(
            @PathVariable UUID postId,
            @RequestParam String content,
            @RequestParam(required = false) UUID parentId,
            @RequestParam(required = false) List<UUID> mentionedUserIds
    ) {
        UUID authorId = authService.getMyInfo();
        return ResponseEntity.ok(commentService.addComment(postId, authorId, content, parentId, mentionedUserIds));
    }

    @GetMapping("/{postId}/comments/total-comments")
    public ResponseEntity<Map<String, Long>> countComments(@PathVariable UUID postId) {
        long totalComments = commentService.countComments(postId);
        return ResponseEntity.ok(Map.of("totalComments", totalComments));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<PageResponse<PostCommentResponse>> getCommentsByPost(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, page, size));
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<PageResponse<PostCommentResponse>> getReplies(
            @PathVariable UUID commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(commentService.findByParentId(commentId, page, size));
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<PostCommentResponse> getCommentById(@PathVariable UUID id) {
        PostCommentResponse response = commentService.getCommentById(id);
        return ResponseEntity.ok(response);
    }
}