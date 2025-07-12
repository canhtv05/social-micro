package com.canhtv05.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileCreationRequest {

    UUID userId;

    @NotBlank
    String username;

    @NotBlank
    String password;
    String avatarUrl;

    @NotBlank
    String email;
    LocalDate dob;
    String city;
}
