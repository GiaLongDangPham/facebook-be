package com.gialong.facebook.postmedia;


import com.gialong.facebook.post.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "post_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String url;

    @Column(length = 20, nullable = false)
    private String mediaType; // IMAGE, VIDEO, GIF...

    private Integer width;
    private Integer height;
    private Integer durationSec;
    private Integer position;

    @Column(length = 500)
    private String thumbnailUrl; // ảnh preview cho video
}