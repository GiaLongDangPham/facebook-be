package com.gialong.facebook.postmedia;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMediaRequest {
    private String url;
    private String mediaType; // IMAGE, VIDEO
    private Integer width;
    private Integer height;
    private Integer durationSec;
    private Integer position;
    private String thumbnailUrl;
}