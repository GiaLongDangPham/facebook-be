package com.gialong.facebook.postcomment;

import com.gialong.facebook.user.UserResponse;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentResponse {
    private UUID id;
    private UUID postId;
    private UserResponse author;
    private String content;
    private UUID parentId;
    private Long repliesCount;
    private Instant createdAt;
    private Instant updatedAt;
}