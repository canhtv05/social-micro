package com.canhtv05.profile.dto.res;

import com.canhtv05.profile.dto.AbstractResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse extends AbstractResponse {

    String userId;
    String username;
    String avatarUrl;
    String email;
    LocalDate dob;
    String city;

    @Builder.Default
    Set<MutualFriendResponse> mutualFriends = new HashSet<>();

    @Builder.Default
    Set<FriendRequestResponse> friendRequests = new HashSet<>();

    @Builder.Default
    Set<FriendRequestResponse> receivedFriendRequests = new HashSet<>();
}
