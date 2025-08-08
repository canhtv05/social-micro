package com.canhtv05.comment.dto.res;

import com.canhtv05.comment.dto.AbstractResponse;
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
public class CommentResponse extends AbstractResponse {

    String postId;
    String userId;
    String content;
    String parentId;

    List<CommentResponse> replies;
}
