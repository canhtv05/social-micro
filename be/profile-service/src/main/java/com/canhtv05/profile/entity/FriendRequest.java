package com.canhtv05.profile.entity;

import com.canhtv05.profile.common.FriendRequestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node("friend_request")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FriendRequest extends AbstractEntity {

    @TargetNode
    UserProfile receiver;

    FriendRequestStatus status;
}
