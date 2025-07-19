package com.canhtv05.post.service;

import java.util.List;
import java.util.Map;

import com.canhtv05.post.dto.ApiResponse;
import com.canhtv05.post.dto.req.PostCreationRequest;
import com.canhtv05.post.dto.req.PostUpdateRequest;
import com.canhtv05.post.dto.req.ReactionRequest;
import com.canhtv05.post.dto.res.PostResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

  PostResponse createPost(PostCreationRequest request, MultipartFile[] file);

  PostResponse updatePost(PostUpdateRequest request);

  Map<String, Object> getMyPosts(Integer page, Integer size);

  PostResponse reactToPost(String postId, ReactionRequest request);
}
