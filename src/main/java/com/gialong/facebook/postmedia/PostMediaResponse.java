package com.gialong.facebook.postmedia;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMediaResponse {
    private UUID id;
    private UUID postId;
    private String url;
    private String mediaType;
    private Integer width;
    private Integer height;
    private Integer durationSec;
    private Integer position;
    private String thumbnailUrl;
}