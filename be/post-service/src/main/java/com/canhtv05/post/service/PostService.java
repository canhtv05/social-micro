package com.canhtv05.post.service;

import com.canhtv05.post.common.ReactionType;
import com.canhtv05.post.dto.req.PostCreationRequest;
import com.canhtv05.post.dto.req.PostUpdateRequest;
import com.canhtv05.post.dto.res.PostListResponse;
import com.canhtv05.post.dto.res.PostResponse;
import com.canhtv05.post.dto.res.UserProfileResponse;
import com.canhtv05.post.dto.res.UserReactionResponse;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface PostService {

  PostResponse createPost(PostCreationRequest request, MultipartFile[] file);

  PostResponse updatePost(String postId, PostUpdateRequest request);

  PostListResponse getAllPostByUserId(Integer page, Integer size, UserProfileResponse user);

  PostResponse reactToPost(String postId, ReactionType reaction);

  UserProfileResponse getUserProfileResponse();

  PostResponse getPostById(String postId);

  Void deletePost(String postId);

  PostListResponse getPosts(Integer page, Integer size);

  Map<ReactionType, Long> getTopReactionsByPostId(String postId);

  List<UserReactionResponse> getUserReactedFromPost(String postId, ReactionType reactionType);
}
