package com.gialong.facebook.userphoto;

import com.gialong.facebook.base.BaseEntity;
import com.gialong.facebook.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPhoto extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String url; // link tới file (avatar, cover, post image)
}