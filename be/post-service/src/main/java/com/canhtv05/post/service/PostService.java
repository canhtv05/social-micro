package com.canhtv05.post.service;

import com.canhtv05.post.dto.req.PostCreationRequest;
import com.canhtv05.post.dto.req.PostUpdateRequest;
import com.canhtv05.post.dto.res.PostListResponse;
import com.canhtv05.post.dto.res.PostResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

  PostResponse createPost(PostCreationRequest request, MultipartFile[] file);

  PostResponse updatePost(PostUpdateRequest request);

  PostListResponse getMyPosts(Integer page, Integer size);

  PostResponse reactToPost(String postId);

  PostListResponse buildPostListWithFiles(String userId, String username, Integer page, Integer size);
}
