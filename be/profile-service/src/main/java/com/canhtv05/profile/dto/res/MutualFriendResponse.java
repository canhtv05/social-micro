package com.canhtv05.profile.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MutualFriendResponse {

    String userId;
    String username;
    String avatarUrl;
}
