package com.canhtv05.post.controller;

import java.util.List;

import com.canhtv05.post.dto.req.PostCreationRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.canhtv05.post.dto.ApiResponse;
import com.canhtv05.post.dto.res.PostResponse;
import com.canhtv05.post.service.PostService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<PostResponse> createPost(
            @ModelAttribute PostCreationRequest request,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        return ApiResponse.<PostResponse>builder()
                .data(postService.createPost(request, files))
                .build();
    }
}
