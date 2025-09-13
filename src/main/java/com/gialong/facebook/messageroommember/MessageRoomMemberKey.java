package com.gialong.facebook.messageroommember;

import com.gialong.facebook.messageroom.MessageRoom;
import com.gialong.facebook.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageRoomMemberKey implements Serializable {
    private User user;
    private MessageRoom messageRoom;
}