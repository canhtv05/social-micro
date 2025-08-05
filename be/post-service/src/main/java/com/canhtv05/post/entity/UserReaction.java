package com.canhtv05.post.entity;

import com.canhtv05.post.common.ReactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "posts")
@Builder
public class UserReaction implements Serializable {

    String userId;
    String username;
    String avatarUrl;
    ReactionType reaction;
}
