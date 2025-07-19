package com.canhtv05.post.service;

import java.util.List;

import com.canhtv05.post.dto.ApiResponse;
import com.canhtv05.post.dto.req.PostCreationRequest;
import com.canhtv05.post.dto.req.PostUpdateRequest;
import com.canhtv05.post.dto.res.PostResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

  PostResponse createPost(PostCreationRequest request, MultipartFile[] file);

  PostResponse updatePost(PostUpdateRequest request);

  ApiResponse<List<PostResponse>> getMyPosts(Integer page, Integer size);
}
