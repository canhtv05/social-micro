package com.canhtv05.post.dto.req;

import com.canhtv05.post.common.ReactionEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionRequest implements Serializable {

    ReactionEnum type;
}
