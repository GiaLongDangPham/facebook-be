package com.gialong.facebook.userprofile;

import com.gialong.facebook.base.BaseEntity;
import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserGender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile extends BaseEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, nullable = false)
    private String username;

    private String fullName;
    private String avatarUrl;
    private String coverUrl;
    private String bio;

    @Enumerated(EnumType.STRING)
    private UserGender gender;
    private LocalDate dob;
    private String location;
    private String website;
}