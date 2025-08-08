package com.canhtv05.comment.service.impl;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.canhtv05.comment.common.ReactionType;
import com.canhtv05.comment.dto.res.FileResponse;
import com.canhtv05.comment.dto.res.CommentListResponse;
import com.canhtv05.comment.entity.UserReaction;
import com.canhtv05.comment.repository.httpclient.FileClient;
import com.canhtv05.comment.util.PostUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.canhtv05.comment.dto.MetaResponse;
import com.canhtv05.comment.dto.PageResponse;
import com.canhtv05.comment.dto.req.CommentCreationRequest;
import com.canhtv05.comment.dto.req.PostUpdateRequest;
import com.canhtv05.comment.dto.res.CommentResponse;
import com.canhtv05.comment.dto.res.UserProfileResponse;
import com.canhtv05.comment.dto.res.UserReactionResponse;
import com.canhtv05.comment.entity.Post;
import com.canhtv05.comment.exception.AppException;
import com.canhtv05.comment.exception.ErrorCode;
import com.canhtv05.comment.mapper.CommentMapper;
import com.canhtv05.comment.repository.PostRepository;
import com.canhtv05.comment.repository.httpclient.UserProfileClient;
import com.canhtv05.comment.service.DateTimeFormatter;
import com.canhtv05.comment.service.CommentService;

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
public class PostServiceImplementation implements CommentService {

    UserProfileClient userProfileClient;
    CommentMapper commentMapper;
    PostRepository postRepository;
    DateTimeFormatter dateTimeFormatter;
    FileClient fileClient;
    PostUtil postUtil;
    UserReactionMapper userReactionMapper;

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "MY_POSTS", allEntries = true),
            @CacheEvict(cacheNames = "FEED", allEntries = true),
            @CacheEvict(cacheNames = "GET_POST_BY_ID", allEntries = true),
    })
    public CommentResponse createPost(CommentCreationRequest request, MultipartFile[] files) {
        UserProfileResponse userProfile = getUserProfileResponse();
        request.setUserId(userProfile.getUserId());

        FileResponse fileResponse;
        try {
            fileResponse = fileClient.uploadFile(files).getData();
        } catch (FeignException _) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        Instant now = Instant.now();
        Post post = commentMapper.toPostCreation(request);
        post.setFileId(fileResponse.getId());
        post.setId(UUID.randomUUID().toString());
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        post.setReactionCounts(0L);
        post.setMyReaction(null);
        post.setHashtags(postUtil.extractHashtags(request.getContent()));

        CommentResponse commentResponse = commentMapper.toPostResponse(postRepository.save(post));
        commentResponse.setFile(fileResponse);
        commentResponse.setUsername(userProfile.getUsername());
        commentResponse.setCreated(dateTimeFormatter.format(now));
        commentResponse.setUserReactions(new ArrayList<>());

        return commentResponse;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "MY_POSTS", key = "#post.userId"),
            @CacheEvict(cacheNames = "GET_POST_BY_ID", key = "#postId"),
            @CacheEvict(cacheNames = "FEED", allEntries = true)
    })
    public CommentResponse updatePost(String postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        post.setContent(request.getContent());
        return commentMapper.toPostResponse(postRepository.save(post));
    }

    @Override
    @Cacheable(value = "MY_POSTS", key = "#page + '-' + #size + '-' + #userProfile.userId")
    public CommentListResponse getAllPostByUserId(Integer page, Integer size, UserProfileResponse userProfile) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Direction.DESC, "createdAt"));
        var pageResponse = postRepository.findAllByUserId(userProfile.getUserId(), pageable);

        Map<String, FileResponse> fileMap = postUtil.getFileResponseMap(pageResponse);
        var result = pageResponse.stream().map(post -> {
            Map<ReactionType, Long> reactionSummary = this.getTopReactionsByPostId(post.getId());
            var p = commentMapper.toPostResponse(post);
            p.setCreated(dateTimeFormatter.format(post.getCreatedAt()));
            p.setUsername(userProfile.getUsername());
            if (post.getFileId() != null) {
                p.setFile(fileMap.get(post.getFileId()));
            }
            p.setReactionSummary(reactionSummary);
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

        return CommentListResponse.builder()
                .meta(metaResponse)
                .data(result)
                .build();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "GET_POST_BY_ID", key = "#postId"),
            @CacheEvict(cacheNames = "FEED", allEntries = true),
            @CacheEvict(cacheNames = "MY_POSTS", allEntries = true)
    })
    public CommentResponse reactToPost(String postId, ReactionType reaction) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        UserProfileResponse me = this.getUserProfileResponse();
        UserProfileResponse userProfileResponse;

        try {
            userProfileResponse = userProfileClient.getUserProfile(post.getUserId()).getData();
        } catch (Exception e) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        List<UserReaction> userReactions = post.getUserReactions();
        if (userReactions == null)
            userReactions = new ArrayList<>();

        Optional<UserReaction> existing = userReactions.stream()
                .filter(u -> u.getUserId().equals(me.getUserId()))
                .findFirst();

        if (existing.isPresent()) {
            if (existing.get().getReaction() == reaction) {
                userReactions.remove(existing.get());
                post.setMyReaction(null);
            } else {
                post.setMyReaction(reaction);
                existing.get().setReaction(reaction);
            }
        } else {
            userReactions.add(UserReaction.builder()
                    .userId(userProfileResponse.getUserId())
                    .username(userProfileResponse.getUsername())
                    .avatarUrl(userProfileResponse.getAvatarUrl())
                    .reaction(reaction)
                    .build());
            post.setMyReaction(reaction);
        }

        post.setUserReactions(userReactions);
        post.setReactionCounts((long) userReactions.size());
        Post result = postRepository.save(post);

        String username = userProfileResponse == null ? null : userProfileResponse.getUsername();
        FileResponse fileResponse = null;
        try {
            fileResponse = fileClient.getFilesByIds(List.of(post.getFileId())).getData().getFirst();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        Map<ReactionType, Long> reactionSummary = this.getTopReactionsByPostId(result.getId());

        CommentResponse response = commentMapper.toPostResponse(result);
        response.setCreated(dateTimeFormatter.format(result.getCreatedAt()));
        response.setUsername(username);
        response.setFile(fileResponse);
        response.setReactionSummary(reactionSummary);
        return response;
    }

    @Override
    @Cacheable(cacheNames = "GET_POST_BY_ID", key = "#postId")
    public CommentResponse getPostById(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        UserProfileResponse userProfileResponse = null;
        try {
            userProfileResponse = userProfileClient.getUserProfile(post.getUserId()).getData();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String username = userProfileResponse == null ? null : userProfileResponse.getUsername();

        Map<ReactionType, Long> reactionSummary = this.getTopReactionsByPostId(post.getId());

        FileResponse fileResponse = null;
        try {
            fileResponse = fileClient.getFilesByIds(List.of(post.getFileId())).getData().getFirst();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        CommentResponse response = commentMapper.toPostResponse(post);
        response.setUsername(username);
        response.setCreated(dateTimeFormatter.format(post.getCreatedAt()));
        response.setReactionSummary(reactionSummary);
        response.setFile(fileResponse);
        return response;
    }

    @Override
    @CacheEvict(cacheNames = { "GET_POST_BY_ID", "MY_POSTS" }, key = "#postId")
    public Void deletePost(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (post.getFileId() != null) {
            try {
                fileClient.deleteById(post.getFileId());
            } catch (FeignException e) {
                log.error(e.getMessage());
            }
        }

        postRepository.delete(post);
        return null;
    }

    @Override
    @Cacheable(value = "FEED", key = "#page + '-' + #size")
    public CommentListResponse getPosts(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findAll(pageable);

        List<String> userIds = posts.getContent().stream()
                .map(Post::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, UserProfileResponse> userMap = Collections.emptyMap();
        try {
            if (!userIds.isEmpty()) {
                var postList = userProfileClient.getUserProfilesByIds(userIds).getData();
                userMap = postList.stream()
                        .collect(Collectors.toMap(UserProfileResponse::getUserId, f -> f));
            }
        } catch (FeignException e) {
            log.error(e.getMessage());
        }

        Map<String, FileResponse> fileMap = postUtil.getFileResponseMap(posts);
        Map<String, UserProfileResponse> finalUserMap = userMap;
        var result = posts.stream().map(post -> {
            Map<ReactionType, Long> reactionSummary = this.getTopReactionsByPostId(post.getId());
            var p = commentMapper.toPostResponse(post);
            p.setCreated(dateTimeFormatter.format(post.getCreatedAt()));
            if (post.getUserId() != null) {
                p.setUsername(finalUserMap.get(post.getUserId()).getUsername());
            }
            if (post.getFileId() != null) {
                p.setFile(fileMap.get(post.getFileId()));
            }
            p.setReactionSummary(getTopReactionsByPostId(post.getId()));
            p.setReactionSummary(reactionSummary);
            return p;
        }).toList();

        MetaResponse metaResponse = MetaResponse.builder()
                .pagination(PageResponse.builder()
                        .currentPage(page)
                        .size(size)
                        .total(posts.getTotalElements())
                        .totalPages(posts.getTotalPages())
                        .count(posts.getContent().size())
                        .build())
                .build();

        return CommentListResponse.builder()
                .meta(metaResponse)
                .data(result)
                .build();
    }

    @Override
    @Cacheable(value = "USER_REACTIONS", key = "#postId + '-' + #reactionType")
    public List<UserReactionResponse> getUserReactedFromPost(String postId, ReactionType reactionType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (post.getUserReactions() == null || post.getUserReactions().isEmpty())
            return new ArrayList<>();

        List<UserReactionResponse> response = post.getUserReactions().stream()
                .filter(reactions -> reactions.getReaction().equals(reactionType))
                .map(userReactionMapper::toReactionResponse)
                .toList();
        return response;
    }

    @Override
    public Map<ReactionType, Long> getTopReactionsByPostId(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        List<UserReaction> userReactions = post.getUserReactions();
        if (userReactions == null || userReactions.isEmpty()) {
            return Collections.emptyMap();
        }

        return userReactions.stream()
                .collect(Collectors.groupingBy(
                        UserReaction::getReaction,
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<ReactionType, Long>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, _) -> e1, LinkedHashMap::new));
    }

    @Override
    public UserProfileResponse getUserProfileResponse() {
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
