package com.canhtv05.post.service.impl;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.canhtv05.post.dto.req.ReactionRequest;
import com.canhtv05.post.dto.res.FileResponse;
import com.canhtv05.post.entity.Reaction;
import com.canhtv05.post.mapper.ReactionMapper;
import com.canhtv05.post.repository.httpclient.FileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    ReactionMapper reactionMapper;

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

        PostResponse postResponse = postMapper.toPostResponse(postRepository.save(post));
        postResponse.setFile(fileResponse);

        return postResponse;
    }

    @Override
    public PostResponse updatePost(PostUpdateRequest request) {
        Post post =
                postRepository.findById(request.getPostId()).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        post.setContent(request.getContent());

        return postMapper.toPostResponse(postRepository.save(post));
    }

    @Cacheable(value = "getMyPosts", key = "#page + ' - ' + #size + ' - ' + #userId")
    @Override
    public Map<String, Object> getMyPosts(Integer page, Integer size) {
        UserProfileResponse userProfile = getUserProfileResponse();
        String userId = userProfile.getUserId();
        String username = userProfile.getUsername();

        log.info("ok ban oiw");

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Direction.DESC, "createdAt"));
        var pageResponse = postRepository.findAllByUserId(userId, pageable);

        var result = pageResponse.stream().map(post -> {
            var p = postMapper.toPostResponse(post);
            p.setCreated(dateTimeFormatter.format(post.getCreatedAt()));
            p.setUsername(username);

            p.setReactions(post.getReactions().stream().map(reactionMapper::toReactionResponse).toList());

            FileResponse fileResponse = null;
            try {
                if (post.getFileId() != null) {
                    fileResponse = fileClient.getFileById(post.getFileId()).getData();
                }
            } catch (FeignException e) {
                log.error(e.getMessage());
            }

            p.setFile(fileResponse);
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


        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        response.put("meta", metaResponse);

        return response;
    }

    @CacheEvict(cacheNames = "getMyPosts", allEntries = true)
    @Override
    public PostResponse reactToPost(String postId, ReactionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));


        Optional<Reaction> existingReaction = post.getReactions().stream()
                .filter(r -> r.getUserId().equals(userId))
                .findFirst();

        Instant now = Instant.now();

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            if (reaction.getType() == request.getType()) {
                post.getReactions().remove(reaction);
                post.setMyReaction(null);
            } else {
                reaction.setType(request.getType());
                post.setMyReaction(request.getType().name());
                reaction.setUpdatedAt(now);
            }
        } else {
            post.getReactions().add(Reaction.builder()
                    .type(request.getType())
                    .createdAt(now)
                    .updatedAt(now)
                    .userId(userId)
                    .build());
            post.setMyReaction(request.getType().name());
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
}
