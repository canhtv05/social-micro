package com.canhtv05.profile.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

import com.canhtv05.profile.common.Gender;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest implements Serializable {

    @NotBlank
    String username;
    String avatarUrl;

    LocalDate dob;
    String city;

    @Builder.Default
    Gender gender = Gender.OTHER;
}
