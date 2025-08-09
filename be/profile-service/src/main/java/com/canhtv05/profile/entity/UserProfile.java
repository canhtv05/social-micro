package com.canhtv05.profile.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.*;

import com.canhtv05.profile.common.Gender;
import com.canhtv05.profile.common.PrivacyLevel;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node("user_profile")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfile extends AbstractEntity {

    @Property("user_id")
    String userId;
    String username;
    String email;
    LocalDate dob;
    String city;
    Gender gender;
    String bio;

    @Property("cover_url")
    String coverUrl;

    @Property("phone_number")
    String phoneNumber;

    @Property("avatar_url")
    String avatarUrl;

    @Builder.Default
    List<String> socialLinks = new ArrayList<>();

    @Builder.Default
    PrivacyLevel profileVisibility = PrivacyLevel.PUBLIC;

    @Builder.Default
    PrivacyLevel friendsListVisibility = PrivacyLevel.PUBLIC;

    @Builder.Default
    PrivacyLevel postsVisibility = PrivacyLevel.PUBLIC;

    @Property("last_online_at")
    Instant lastOnlineAt;

    @Builder.Default
    Long followersCount = 0L;

    @Builder.Default
    Long followingCount = 0L;

    @Builder.Default
    @Relationship(type = "FRIEND_REQUEST", direction = Relationship.Direction.OUTGOING)
    Set<FriendRequest> friendRequests = new HashSet<>();

    @Builder.Default
    @Relationship(type = "FRIEND_REQUEST", direction = Relationship.Direction.INCOMING)
    Set<FriendRequest> receivedFriendRequests = new HashSet<>();

    @Builder.Default
    @Relationship(type = "FRIENDS_WITH")
    Set<UserProfile> friends = new HashSet<>();

    @Builder.Default
    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    Set<UserProfile> following = new HashSet<>();

    @Builder.Default
    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.INCOMING)
    Set<UserProfile> followers = new HashSet<>();
}
