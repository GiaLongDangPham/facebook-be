package com.gialong.facebook.message;

import com.gialong.facebook.base.BaseEntity;
import com.gialong.facebook.conversation.Conversation;
import com.gialong.facebook.messagereceipt.MessageReceipt;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String attachmentUrl;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageReceipt> receipts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent_message_content_id")
    private Message parentMessageContent;

    @OneToMany(mappedBy = "parentMessageContent", cascade = CascadeType.ALL)
    private List<Message> replies;
}