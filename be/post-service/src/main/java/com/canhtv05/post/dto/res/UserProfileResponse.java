package com.canhtv05.post.dto.res;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse implements Serializable {

    String userId;
    String username;
    String avatarUrl;
    String email;
    LocalDate dob;
    String city;
}
