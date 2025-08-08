package com.canhtv05.comment.dto.res;

import com.canhtv05.comment.common.ReactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserReactionResponse implements Serializable {

    String userId;
    String username;
    String avatarUrl;
    ReactionType reaction;
}
