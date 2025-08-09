package com.canhtv05.profile.dto.req;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FollowRequest implements Serializable {

    @NotBlank
    String followerId;

    @NotBlank
    String userTargetId;
}
