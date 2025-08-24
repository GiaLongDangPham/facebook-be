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

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private String url;
    private String mediaType;
    private Integer width;
    private Integer height;
    private Integer durationSec;
    private Integer position;
}