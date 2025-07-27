package com.canhtv05.post.dto.res;

import com.canhtv05.post.common.ReactionEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionResponse implements Serializable {

    String userId;
    ReactionEnum type;
    Instant createdAt;
    Instant updatedAt;
}
