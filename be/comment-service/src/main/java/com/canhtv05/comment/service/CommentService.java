package com.canhtv05.comment.service;

import com.canhtv05.comment.dto.req.CommentCreationRequest;
import com.canhtv05.comment.dto.req.PostUpdateRequest;
import com.canhtv05.comment.dto.res.CommentListResponse;
import com.canhtv05.comment.dto.res.CommentResponse;
import com.canhtv05.comment.dto.res.UserProfileResponse;
import com.canhtv05.comment.dto.res.UserReactionResponse;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface CommentService {

  CommentResponse createPost(CommentCreationRequest request, MultipartFile[] file);

  CommentResponse updatePost(String postId, PostUpdateRequest request);

  CommentListResponse getAllPostByUserId(Integer page, Integer size, UserProfileResponse user);

  CommentResponse reactToPost(String postId, ReactionType reaction);

  UserProfileResponse getUserProfileResponse();

  CommentResponse getPostById(String postId);

  Void deletePost(String postId);

  CommentListResponse getPosts(Integer page, Integer size);

  Map<ReactionType, Long> getTopReactionsByPostId(String postId);

  List<UserReactionResponse> getUserReactedFromPost(String postId, ReactionType reactionType);
}
