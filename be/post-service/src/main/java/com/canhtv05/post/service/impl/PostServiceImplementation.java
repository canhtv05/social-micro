package com.canhtv05.post.service.impl;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.canhtv05.post.dto.res.FileResponse;
import com.canhtv05.post.dto.res.PostListResponse;
import com.canhtv05.post.entity.UserReaction;
import com.canhtv05.post.mapper.UserReactionMapper;
import com.canhtv05.post.repository.httpclient.FileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImplementation implements PostService {

    UserProfileClient userProfileClient;
    PostMapper postMapper;
    PostRepository postRepository;
    DateTimeFormatter dateTimeFormatter;
    FileClient fileClient;

    @Lazy
    PostService postService;

    @Override
    public PostResponse createPost(PostCreationRequest request, MultipartFile[] files) {
        UserProfileResponse userProfile = getUserProfileResponse();
        request.setUserId(userProfile.getUserId());

        FileResponse fileResponse = null;
        try {
            fileResponse = fileClient.uploadFile(files).getData();
        } catch (FeignException _) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        Instant now = Instant.now();
        Post post = postMapper.toPostCreation(request);
        post.setFileId(fileResponse.getId());
        post.setId(UUID.randomUUID().toString());
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        post.setLikesCount(0L);
        post.setIsLiked(false);

        PostResponse postResponse = postMapper.toPostResponse(postRepository.save(post));
        postResponse.setFile(fileResponse);
        postResponse.setUsername(userProfile.getUsername());
        postResponse.setCreated(dateTimeFormatter.format(now));
        postResponse.setUserReactions(new ArrayList<>());

        return postResponse;
    }

    @Override
    public PostResponse updatePost(PostUpdateRequest request) {
        Post post =
                postRepository.findById(request.getPostId()).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        post.setContent(request.getContent());

        return postMapper.toPostResponse(postRepository.save(post));
    }

    @Override
    public PostListResponse getMyPosts(Integer page, Integer size) {
        UserProfileResponse userProfile = getUserProfileResponse();
        return postService.buildPostListWithFiles(userProfile.getUserId(), userProfile.getUsername(), page, size);
    }

    @CacheEvict(cacheNames = "getMyPosts", allEntries = true)
    @Override
    public PostResponse reactToPost(String postId) {
        UserProfileResponse userProfile = getUserProfileResponse();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        List<UserReaction> userReactions = post.getUserReactions();
        if (userReactions == null) userReactions = new ArrayList<>();

        Optional<UserReaction> existing = userReactions.stream()
                .filter(u -> u.getUserId().equals(userProfile.getUserId()))
                .findFirst();

        if (existing.isPresent()) {
            userReactions.remove(existing.get());
            post.setIsLiked(false);
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            post.setUserReactions(userReactions);
        } else {
            userReactions.add(UserReaction.builder()
                    .userId(userProfile.getUserId())
                    .username(userProfile.getUsername())
                    .avatarUrl(userProfile.getAvatarUrl())
                    .build());
            post.setIsLiked(true);
            post.setLikesCount(Math.max(0, post.getLikesCount() + 1));
            post.setUserReactions(userReactions);
        }

        return postMapper.toPostResponse(postRepository.save(post));
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

        if (userProfile == null)
            throw new AppException(ErrorCode.USER_NOT_FOUND);

        return userProfile;
    }

    @Override
    @Cacheable(value = "getMyPosts", key = "#page + '-' + #size + '-' + #userId")
    public PostListResponse buildPostListWithFiles(String userId, String username ,Integer page, Integer size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Direction.DESC, "createdAt"));
        var pageResponse = postRepository.findAllByUserId(userId, pageable);

        List<String> fileIds = pageResponse.getContent().stream()
                .map(Post::getFileId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // tránh gọi feign nhiều lần
        Map<String, FileResponse> fileMap = Collections.emptyMap();
        try {
            if (!fileIds.isEmpty()) {
                var fileList = fileClient.getFilesByIds(fileIds).getData();
                fileMap = fileList.stream()
                        .collect(Collectors.toMap(FileResponse::getId, f -> f));
            }
        } catch (FeignException e) {
            log.error(e.getMessage());
        }

        Map<String, FileResponse> finalFileMap = fileMap;
        var result = pageResponse.stream().map(post -> {
            var p = postMapper.toPostResponse(post);
            p.setCreated(dateTimeFormatter.format(post.getCreatedAt()));
            p.setUsername(username);
            if (post.getFileId() != null) {
                p.setFile(finalFileMap.get(post.getFileId()));
            }
            return p;
        }).toList();

        MetaResponse metaResponse = MetaResponse.builder()
                .pagination(PageResponse.builder()
                        .currentPage(page)
                        .size(size)
                        .total(pageResponse.getTotalElements())
                        .totalPages(pageResponse.getTotalPages())
                        .count(pageResponse.getContent().size())
                        .build())
                .build();


        return PostListResponse.builder()
                .meta(metaResponse)
                .data(result)
                .build();
    }
}
