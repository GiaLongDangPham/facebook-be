package com.gialong.facebook.postcommentlike;

import com.gialong.facebook.postcomment.PostComment;
import com.gialong.facebook.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class PkUserCommentLike implements Serializable {
    private PostComment comment;
    private User user;
}
