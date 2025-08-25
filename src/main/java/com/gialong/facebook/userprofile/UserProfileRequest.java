package com.gialong.facebook.userprofile;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {
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