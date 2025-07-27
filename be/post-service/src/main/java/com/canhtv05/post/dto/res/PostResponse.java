package com.canhtv05.post.dto.res;

import com.canhtv05.post.dto.AbstractResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class PostResponse extends AbstractResponse {

    String userId;
    String content;
    String username;
    String created;

    @Builder.Default
    List<UserReactionResponse> userReactions = new ArrayList<>();

    @Builder.Default
    Boolean isLiked = false;

    @Builder.Default
    Long likesCount = 0L;
    FileResponse file;
}
