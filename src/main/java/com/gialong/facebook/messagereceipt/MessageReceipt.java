package com.gialong.facebook.messagereceipt;


import com.gialong.facebook.message.Message;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "message_receipts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(MessageReceiptId.class)
public class MessageReceipt {
    @Id
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant seenAt;
    private Instant deliveredAt;
}