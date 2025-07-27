package com.canhtv05.post.dto.res;

import com.canhtv05.post.dto.MetaResponse;
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
public class PostListResponse implements Serializable {

    List<PostResponse> data;
    MetaResponse meta;
}