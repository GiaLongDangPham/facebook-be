package com.gialong.facebook.notification;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class PkNotification implements Serializable {
    private UUID targetId;
    private ActionEnum actionType;
}
