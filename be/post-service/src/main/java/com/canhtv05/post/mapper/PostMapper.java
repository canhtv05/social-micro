package com.canhtv05.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.canhtv05.post.dto.req.PostCreationRequest;
import com.canhtv05.post.dto.res.PostResponse;
import com.canhtv05.post.entity.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "file", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "reactionSummary", ignore = true)
    PostResponse toPostResponse(Post post);

    @Mapping(target = "hashtags", ignore = true)
    @Mapping(target = "fileId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reactionCounts", ignore = true)
    @Mapping(target = "myReaction", ignore = true)
    @Mapping(target = "userReactions", ignore = true)
    Post toPostCreation(PostCreationRequest request);
}
