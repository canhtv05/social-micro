package com.canhtv05.post.dto.res;

import java.time.Instant;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
  String id;
  String userId;
  String content;
  String username;
  String created;
  List<String> images;
  Instant createdAt;
  Instant updatedAt;
}
