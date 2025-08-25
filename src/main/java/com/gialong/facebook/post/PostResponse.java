package com.gialong.facebook.post;

import com.gialong.facebook.postmedia.PostMediaResponse;
import com.gialong.facebook.user.UserResponse;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private UUID id;
    private UserResponse author;
    private String content;
    private String privacy;
    private Boolean commentLocked;
    private List<PostMediaResponse> mediaList;
    private Integer likeCount;
    private Integer commentCount;
    private Instant createdAt;
    private Instant updatedAt;
}