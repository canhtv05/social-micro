package com.canhtv05.post.dto.res;

import com.canhtv05.post.dto.AbstractResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

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
    List<ReactionResponse> reactions;
    String myReaction;
    Long countReactions;
    List<TopReactionsResponse> topReactions;
    FileResponse file;
}
