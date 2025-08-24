package com.gialong.facebook.conversationmember;

import com.gialong.facebook.conversation.Conversation;
import com.gialong.facebook.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class ConversationMemberId implements Serializable {
    private Conversation conversation;
    private User user;
}