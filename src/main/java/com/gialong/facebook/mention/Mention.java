package com.gialong.facebook.mention;


import com.gialong.facebook.post.Post;
import com.gialong.facebook.postcomment.PostComment;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "mentions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mention {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private PostComment comment;

    @ManyToOne
    @JoinColumn(name = "mentioned_user")
    private User mentionedUser;
}