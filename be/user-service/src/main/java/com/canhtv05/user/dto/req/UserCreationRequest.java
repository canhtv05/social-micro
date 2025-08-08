package com.canhtv05.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

import com.canhtv05.user.common.Gender;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest implements Serializable {

    @NotBlank
    String username;

    @NotBlank
    String password;
    String avatarUrl;

    @NotBlank
    String email;
    LocalDate dob;
    String city;

    @Builder.Default
    Gender gender = Gender.OTHER;
}
