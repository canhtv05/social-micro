package com.canhtv05.profile.dto.res;

import com.canhtv05.profile.common.Gender;
import com.canhtv05.profile.common.PrivacyLevel;
import com.canhtv05.profile.dto.AbstractResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse extends AbstractResponse {

    String userId;
    String username;
    String email;
    LocalDate dob;
    String city;
    Gender gender;
    String bio;
    String coverUrl;
    String phoneNumber;
    String avatarUrl;

    @Builder.Default
    List<String> socialLinks = new ArrayList<>();

    PrivacyLevel profileVisibility;
    PrivacyLevel friendsListVisibility;
    PrivacyLevel postsVisibility;
    Instant lastOnlineAt;
    Long followersCount;
    Long followingCount;

    @Builder.Default
    Set<FriendRequestResponse> friendRequests = new HashSet<>();

    @Builder.Default
    Set<FriendRequestResponse> receivedFriendRequests = new HashSet<>();

    @Builder.Default
    Set<MutualFriendResponse> mutualFriends = new HashSet<>();

    @Builder.Default
    Set<FriendSummaryResponse> friends = new HashSet<>();

    @Builder.Default
    Set<FriendSummaryResponse> following = new HashSet<>();

    @Builder.Default
    Set<FriendSummaryResponse> followers = new HashSet<>();
}
