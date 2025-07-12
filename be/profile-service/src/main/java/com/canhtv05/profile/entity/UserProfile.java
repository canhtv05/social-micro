package com.canhtv05.profile.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDate;
import java.util.HashSet;
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
    String password;
    String email;
    LocalDate dob;
    String city;

    @Property("avatar_url")
    String avatarUrl;

    @Builder.Default
    @Relationship(type = "FRIEND_REQUEST", direction = Relationship.Direction.OUTGOING)
    Set<FriendRequest> friendRequests = new HashSet<>();

    @Builder.Default
    @Relationship(type = "FRIEND_REQUEST", direction = Relationship.Direction.INCOMING)
    Set<FriendRequest> receivedFriendRequests = new HashSet<>();

    @Relationship(type = "FRIENDS_WITH")
    Set<UserProfile> friends;
}
