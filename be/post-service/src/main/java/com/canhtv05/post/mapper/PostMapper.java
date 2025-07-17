package com.canhtv05.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.canhtv05.post.dto.req.PostCreationRequest;
import com.canhtv05.post.dto.req.PostUpdateRequest;
import com.canhtv05.post.dto.res.PostResponse;
import com.canhtv05.post.entity.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {

  PostResponse toPostResponse(Post post);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "images", ignore = true)
  Post toPostCreation(PostCreationRequest request);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "images", ignore = true)
  Post toPostUpdate(PostUpdateRequest request);
}
