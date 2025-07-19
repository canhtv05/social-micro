package com.canhtv05.post.controller;

import java.util.List;
import java.util.Map;

import com.canhtv05.post.dto.MetaResponse;
import com.canhtv05.post.dto.req.PostCreationRequest;
import com.canhtv05.post.dto.req.ReactionRequest;
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

    @SuppressWarnings("unchecked")
    @GetMapping("/me")
    public ApiResponse<List<PostResponse>> getMyPosts(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "15", required = false) Integer size) {

        Map<String, Object> response = postService.getMyPosts(page, size);

        return ApiResponse.<List<PostResponse>>builder()
                .data((List<PostResponse>) response.get("data"))
                .meta((MetaResponse) response.get("meta"))
                .build();
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

    @PutMapping("/{postId}/reaction")
    public ApiResponse<PostResponse> reactToPost(@PathVariable(name = "postId") String postId,
                                                 @RequestBody ReactionRequest request) {
        return ApiResponse.<PostResponse>builder()
                .data(postService.reactToPost(postId, request))
                .build();
    }
}
