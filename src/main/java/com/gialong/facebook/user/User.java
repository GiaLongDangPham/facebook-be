package com.gialong.facebook.user;

import com.gialong.facebook.auth.token.Token;
import com.gialong.facebook.base.BaseEntity;
import com.gialong.facebook.mention.Mention;
import com.gialong.facebook.messagecontent.MessageContent;
import com.gialong.facebook.messageroom.MessageRoom;
import com.gialong.facebook.messageroommember.MessageRoomMember;
import com.gialong.facebook.post.Post;
import com.gialong.facebook.postcomment.PostComment;
import com.gialong.facebook.postcommentlike.PostCommentLike;
import com.gialong.facebook.postlike.PostLike;
import com.gialong.facebook.report.Report;
import com.gialong.facebook.userblock.UserBlock;
import com.gialong.facebook.userfriend.UserFriend;
import com.gialong.facebook.userphoto.UserPhoto;
import com.gialong.facebook.userprofile.UserProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.usertype.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // last offline
    private Long lastOffline;

    @Enumerated(EnumType.STRING)
    private Role role;

    //Profile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    // Friendship
    // Lời mời kết bạn do mình gửi
    @OneToMany(mappedBy = "requester")
    private Set<UserFriend> sentFriendRequests;

    // Lời mời kết bạn mà mình nhận
    @OneToMany(mappedBy = "addressee")
    private Set<UserFriend> receivedFriendRequests;

    //Block
    @OneToMany(mappedBy = "blocker")
    private Set<UserBlock> blockers;

    @OneToMany(mappedBy = "blocked")
    private Set<UserBlock> blockedUsers;

    // Message
    @OneToMany(mappedBy = "user")
    private Set<MessageContent> messageContents;

    // MessageRoom
    @OneToMany(mappedBy = "createdBy")
    private Set<MessageRoom> messageRooms;

    // MessageRoomMember
    @OneToMany(mappedBy = "user")
    private Set<MessageRoomMember> messageRoomMembers;

    //Photo
    @OneToMany(mappedBy = "user")
    private Set<UserPhoto> photos;
    //Post
    @OneToMany(mappedBy = "author")
    private Set<Post> posts;

    //Post Comment
    @OneToMany(mappedBy = "author")
    private Set<PostComment> postComments;

    //Post Comment Likes
    @OneToMany(mappedBy = "user")
    private Set<PostCommentLike> commentLikes;

    //Post Like
    @OneToMany(mappedBy = "user")
    private Set<PostLike> postLikes;

    //Mention
    @OneToMany(mappedBy = "mentionedUser")
    private Set<Mention> mentions;

    //Report
    @OneToMany(mappedBy = "user")
    private Set<Report> reports;

    //Refresh Tokens
    @OneToMany(mappedBy = "user")
    private Set<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}