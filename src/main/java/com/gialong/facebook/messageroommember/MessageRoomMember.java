package com.gialong.facebook.messageroommember;


import com.gialong.facebook.messageroom.MessageRoom;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_room_member")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(MessageRoomMemberKey.class)
public class MessageRoomMember {
    @Id
    @ManyToOne
    @JoinColumn(name = "message_room_id")
    private MessageRoom messageRoom;

    @Id
    @ManyToOne
    @JoinColumn(name = "username")
    private User user;

    private Boolean isAdmin;

    private LocalDateTime lastSeen;
}