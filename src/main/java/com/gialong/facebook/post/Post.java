package com.gialong.facebook.post;

import com.gialong.facebook.base.BaseEntity;
import com.gialong.facebook.mention.Mention;
import com.gialong.facebook.postcomment.PostComment;
import com.gialong.facebook.postlike.PostLike;
import com.gialong.facebook.postmedia.PostMedia;
import com.gialong.facebook.report.Report;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(columnDefinition = "text")
    private String caption;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean commentLocked;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostMedia> mediaList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Mention> mentions;

    @OneToMany(mappedBy = "post")
    private List<Report> reports;

}