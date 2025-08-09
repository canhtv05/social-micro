package com.canhtv05.profile.dto.res;

import com.canhtv05.profile.common.FriendRequestStatus;
import com.canhtv05.profile.dto.AbstractResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendRequestResponse extends AbstractResponse {

    String senderUserId;
    String senderUsername;
    String senderAvatarUrl;

    String receiverUserId;
    String receiverUsername;
    String receiverAvatarUrl;

    FriendRequestStatus status;
}
