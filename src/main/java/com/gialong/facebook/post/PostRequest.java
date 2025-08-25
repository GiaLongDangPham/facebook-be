package com.gialong.facebook.post;

import com.gialong.facebook.postmedia.PostMediaRequest;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {
    private String content;
    private String privacy; // PUBLIC, FRIENDS, PRIVATE
    private Boolean commentLocked;
    private List<PostMediaRequest> mediaList;
}