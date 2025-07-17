package com.canhtv05.post.service.impl;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.canhtv05.post.dto.ApiResponse;
import com.canhtv05.post.dto.MetaResponse;
import com.canhtv05.post.dto.PageResponse;
import com.canhtv05.post.dto.req.PostCreationRequest;
import com.canhtv05.post.dto.req.PostUpdateRequest;
import com.canhtv05.post.dto.res.PostResponse;
import com.canhtv05.post.dto.res.UserProfileResponse;
import com.canhtv05.post.entity.Post;
import com.canhtv05.post.exception.AppException;
import com.canhtv05.post.exception.ErrorCode;
import com.canhtv05.post.mapper.PostMapper;
import com.canhtv05.post.repository.PostRepository;
import com.canhtv05.post.repository.httpclient.UserProfileClient;
import com.canhtv05.post.service.DateTimeFormatter;
import com.canhtv05.post.service.PostService;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImplementation implements PostService {

    UserProfileClient userProfileClient;
    PostMapper postMapper;
    PostRepository postRepository;
    DateTimeFormatter dateTimeFormatter;

    @Override
    public PostResponse createPost(PostCreationRequest request) {
        UserProfileResponse userProfile = getUserProfileResponse();
        if (request.getUserId() != userProfile.getUserId()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Post post = postMapper.toPostCreation(request);

        return postMapper.toPostResponse(postRepository.save(post));
    }

    @Override
    public PostResponse updatePost(PostUpdateRequest request) {
        Post post =
                postRepository.findById(request.getPostId()).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        post.setContent(request.getContent());

        return postMapper.toPostResponse(postRepository.save(post));
    }

    @Override
    public ApiResponse<List<PostResponse>> getMyPosts(Integer page, Integer size) {
        UserProfileResponse userProfile = getUserProfileResponse();

        log.info("userProfile: {}", userProfile.getUsername());

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Direction.DESC, "createdAt"));
        var pageResponse = postRepository.findAllByUserId(userProfile.getUserId(), pageable);

        String username = userProfile.getUsername();

        var result = pageResponse.stream().map(post -> {
            var p = postMapper.toPostResponse(post);
            p.setCreated(dateTimeFormatter.format(p.getCreatedAt()));
            p.setUsername(username);
            return p;
        }).toList();

        MetaResponse<?> metaResponse =
                MetaResponse.builder().page(PageResponse.builder().currentPage(page + 1).pageSize(size).totalElements(pageResponse.getTotalElements()).totalPages(pageResponse.getTotalPages()).build()).build();

        return ApiResponse.<List<PostResponse>>builder().data(result).meta(metaResponse).build();
    }

    private UserProfileResponse getUserProfileResponse() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        UserProfileResponse userProfile = null;
        try {
            userProfile = userProfileClient.getUserProfile(userId).getData();
        } catch (FeignException _) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        return userProfile;
    }
}
