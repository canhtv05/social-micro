package com.canhtv05.profile.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import com.canhtv05.profile.common.Gender;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileCreationRequest implements Serializable {

    UUID userId;

    @NotBlank
    String username;
    String avatarUrl;

    @NotBlank
    String email;
    LocalDate dob;
    String city;
    Gender gender;
}
