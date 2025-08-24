package com.gialong.facebook.userprofile;

import com.gialong.facebook.user.UserGender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
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
