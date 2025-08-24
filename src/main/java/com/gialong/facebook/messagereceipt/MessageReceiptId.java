package com.gialong.facebook.messagereceipt;

import com.gialong.facebook.message.Message;
import com.gialong.facebook.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageReceiptId implements Serializable {
    private Message message;
    private User user;
}