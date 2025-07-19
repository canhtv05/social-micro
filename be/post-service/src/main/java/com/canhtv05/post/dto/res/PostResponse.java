package com.canhtv05.post.dto.res;

import com.canhtv05.post.dto.AbstractResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse extends AbstractResponse {

    String userId;
    String content;
    String username;
    String created;
    FileResponse file;
}
