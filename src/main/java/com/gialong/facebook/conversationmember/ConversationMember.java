package com.gialong.facebook.conversationmember;


import com.gialong.facebook.conversation.Conversation;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "conversation_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ConversationMemberId.class)
public class ConversationMember {
    @Id
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String role;
    private Instant joinedAt;
}