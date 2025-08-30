package com.gialong.facebook.postcommentlike;

import com.gialong.facebook.postcomment.PostComment;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_comment_likes")
@IdClass(PkUserCommentLike.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentLike {
    @Id
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private PostComment comment;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
