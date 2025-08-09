//package com.canhtv05.comment.controller;
//
//import java.util.List;
//
//import com.canhtv05.comment.dto.req.CommentCreationRequest;
//import com.canhtv05.comment.dto.req.PostUpdateRequest;
//import com.canhtv05.comment.dto.res.CommentListResponse;
//import com.canhtv05.comment.dto.res.UserProfileResponse;
////import com.canhtv05.comment.util.PostUtil;
//import jakarta.validation.Valid;
//import org.springframework.http.MediaType;
//import com.canhtv05.comment.dto.res.UserReactionResponse;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import com.canhtv05.comment.dto.ApiResponse;
//import com.canhtv05.comment.dto.res.CommentResponse;
//import com.canhtv05.comment.service.CommentService;
//
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class PostController {
//
//        CommentService postService;
//        PostUtil postUtil;
//
//        @PreAuthorize("isAuthenticated()")
//        @GetMapping("/me")
//        public ApiResponse<List<CommentResponse>> getMyPosts(
//                        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
//                        @RequestParam(name = "size", defaultValue = "15", required = false) Integer size) {
//
//                UserProfileResponse userProfileResponse = postService.getUserProfileResponse();
//                CommentListResponse response = postService.getAllPostByUserId(page, size, userProfileResponse);
//
//                return ApiResponse.<List<CommentResponse>>builder()
//                                .data(response.getData())
//                                .meta(response.getMeta())
//                                .build();
//        }
//
//        @GetMapping("/user/{userId}")
//        public ApiResponse<List<CommentResponse>> getAllPostByUserId(
//                        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
//                        @RequestParam(name = "size", defaultValue = "15", required = false) Integer size,
//                        @PathVariable(name = "userId") String userId) {
//
//                UserProfileResponse userProfileResponse = postUtil.getUserProfileResponse(userId);
//                CommentListResponse response = postService.getAllPostByUserId(page, size, userProfileResponse);
//
//                return ApiResponse.<List<CommentResponse>>builder()
//                                .data(response.getData())
//                                .meta(response.getMeta())
//                                .build();
//        }
//
//        @PreAuthorize("isAuthenticated()")
//        @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//        public ApiResponse<CommentResponse> createPost(
//                        @Valid @ModelAttribute CommentCreationRequest request,
//                        @RequestPart(value = "files", required = false) MultipartFile[] files) {
//                return ApiResponse.<CommentResponse>builder()
//                                .data(postService.createPost(request, files))
//                                .code(201)
//                                .build();
//        }
//
//        @PreAuthorize("isAuthenticated()")
//        @PutMapping("/reaction/{postId}")
//        public ApiResponse<CommentResponse> reactToPost(@PathVariable(name = "postId") String postId,
//                                                        @RequestParam ReactionType reactionType) {
//                return ApiResponse.<CommentResponse>builder()
//                                .data(postService.reactToPost(postId, reactionType))
//                                .build();
//        }
//
//        @PreAuthorize("isAuthenticated()")
//        @PutMapping("/{postId}")
//        public ApiResponse<CommentResponse> updateContentPost(@PathVariable(name = "postId") String postId,
//                                                              @Valid @RequestBody PostUpdateRequest request) {
//                return ApiResponse.<CommentResponse>builder()
//                                .data(postService.updatePost(postId, request))
//                                .build();
//        }
//
//        @GetMapping("/{postId}")
//        public ApiResponse<CommentResponse> getPostById(@PathVariable(name = "postId") String postId) {
//                return ApiResponse.<CommentResponse>builder()
//                                .data(postService.getPostById(postId))
//                                .build();
//        }
//
//        @PreAuthorize("isAuthenticated()")
//        @DeleteMapping("/{postId}")
//        public ApiResponse<Void> deletePost(@PathVariable(name = "postId") String postId) {
//                return ApiResponse.<Void>builder()
//                                .data(postService.deletePost(postId))
//                                .message("deleted")
//                                .build();
//        }
//
//        @GetMapping("/feed")
//        public ApiResponse<List<CommentResponse>> getFeeds(
//                        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
//                        @RequestParam(name = "size", defaultValue = "15", required = false) Integer size) {
//
//                CommentListResponse response = postService.getPosts(page, size);
//                return ApiResponse.<List<CommentResponse>>builder()
//                                .data(response.getData())
//                                .meta(response.getMeta())
//                                .build();
//        }
//
//        @GetMapping("/reactions/{postId}")
//        public ApiResponse<List<UserReactionResponse>> getUserReactedFromPost(@PathVariable String postId,
//                        @RequestParam ReactionType reactionType) {
//                return ApiResponse.<List<UserReactionResponse>>builder()
//                                .data(postService.getUserReactedFromPost(postId, reactionType))
//                                .build();
//        }
//}
