package com.canhtv05.comment.dto.res;

import com.canhtv05.comment.dto.MetaResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentListResponse implements Serializable {

    List<CommentResponse> data;
    MetaResponse meta;
}