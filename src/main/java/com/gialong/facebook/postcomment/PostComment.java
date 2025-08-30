package com.gialong.facebook.postcomment;


import com.gialong.facebook.base.BaseEntity;
import com.gialong.facebook.mention.Mention;
import com.gialong.facebook.post.Post;
import com.gialong.facebook.postcommentlike.PostCommentLike;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "post_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "comment")
    private List<Mention> mentions;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> replies = new ArrayList<>();

    @OneToMany(mappedBy = "comment")
    private Set<PostCommentLike> commentLikes;
}