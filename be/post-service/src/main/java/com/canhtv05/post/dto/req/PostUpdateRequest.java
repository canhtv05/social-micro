package com.canhtv05.post.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostUpdateRequest {

  @NotBlank
  String postId;

  @NotBlank
  String content;
}
