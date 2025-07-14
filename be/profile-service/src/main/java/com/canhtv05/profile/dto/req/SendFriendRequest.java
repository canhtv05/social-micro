package com.canhtv05.profile.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendFriendRequest {

    @NotBlank
    String senderUserId;

    @NotBlank
    String receiverUserId;
}
