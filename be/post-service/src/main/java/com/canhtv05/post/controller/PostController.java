package com.canhtv05.post.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.canhtv05.post.dto.ApiResponse;
import com.canhtv05.post.dto.res.PostResponse;
import com.canhtv05.post.service.PostService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {

  PostService postService;

  @GetMapping("/me")
  public ApiResponse<List<PostResponse>> getMyPosts(
      @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
      @RequestParam(name = "size", defaultValue = "15", required = false) Integer size) {

    return postService.getMyPosts(page, size);
  }
}
