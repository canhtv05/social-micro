package com.canhtv05.profile.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendSummaryResponse {

  String userId;
  String username;
  String avatarUrl;
}
