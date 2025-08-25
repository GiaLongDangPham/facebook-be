package com.gialong.facebook.userphoto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPhotoResponse{

    private UUID id;

    private UUID userId;

    private String url; // link tới file (avatar, cover, post image)
}