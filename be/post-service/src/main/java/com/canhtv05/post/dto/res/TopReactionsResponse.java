package com.canhtv05.post.dto.res;

import com.canhtv05.post.common.ReactionEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopReactionsResponse implements Serializable {

    ReactionEnum type;
    Long count;
    List<UserProfileResponse> users;
}
