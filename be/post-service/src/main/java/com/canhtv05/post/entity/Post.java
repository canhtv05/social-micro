package com.canhtv05.post.entity;

import java.time.Instant;
import java.util.List;

import com.canhtv05.post.common.ReactionType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "posts")
public class Post {

    @Id
    String id;
    String userId;
    String content;
    List<String> hashtags;
    String fileId;
    ReactionType myReaction;
    Long reactionCounts;
    List<UserReaction> userReactions;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;
}
