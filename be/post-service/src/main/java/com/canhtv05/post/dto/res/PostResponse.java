package com.canhtv05.post.dto.res;

import com.canhtv05.post.common.ReactionType;
import com.canhtv05.post.dto.AbstractResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    ReactionType myReaction;

    @Builder.Default
    List<UserReactionResponse> userReactions = Collections.emptyList();

    @Builder.Default
    List<String> hashtags = Collections.emptyList();

    @Builder.Default
    Long reactionCounts = 0L;
    FileResponse file;

    Map<ReactionType, Long> reactionSummary;
}
