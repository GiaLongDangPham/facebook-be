package com.gialong.facebook.userprofile;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {
    private UUID userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String coverUrl;
    private String bio;
    private String gender;
    private LocalDate dob;
    private String location;
    private String website;
}