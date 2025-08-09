package com.canhtv05.profile.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.canhtv05.profile.common.Gender;
import com.canhtv05.profile.common.PrivacyLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileUpdateRequest implements Serializable {

    UUID userId;

    @NotBlank
    String username;
    String avatarUrl;

    @NotBlank
    String email;
    LocalDate dob;
    String city;
    Gender gender;
    String coverUrl;
    String phoneNumber;
    String bio;

    @Builder.Default
    List<String> socialLinks = new ArrayList<>();

    @Builder.Default
    PrivacyLevel profileVisibility = PrivacyLevel.PUBLIC;

    @Builder.Default
    PrivacyLevel friendsListVisibility = PrivacyLevel.PUBLIC;

    @Builder.Default
    PrivacyLevel postsVisibility = PrivacyLevel.PUBLIC;
}
