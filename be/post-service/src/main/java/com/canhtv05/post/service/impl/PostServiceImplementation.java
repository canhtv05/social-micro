package com.canhtv05.post.service.impl;

import java.util.List;

import com.canhtv05.post.dto.res.FileResponse;
import com.canhtv05.post.repository.httpclient.FileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
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

        Post post = postMapper.toPostCreation(request);
        post.setFileId(fileResponse.getId());

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

    @Cacheable(value = "getMyPosts")
    @Override
    public ApiResponse<List<PostResponse>> getMyPosts(Integer page, Integer size) {
        UserProfileResponse userProfile = getUserProfileResponse();
        String userId = userProfile.getUserId();
        String username = userProfile.getUsername();

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Direction.DESC, "createdAt"));
        var pageResponse = postRepository.findAllByUserId(userId, pageable);

        var result = pageResponse.stream().map(post -> {
            var p = postMapper.toPostResponse(post);
            p.setCreated(dateTimeFormatter.format(p.getCreatedAt()));
            p.setUsername(username);

            log.error("Post : {}", post.getFileId());

            FileResponse fileResponse = null;
            try {
                fileResponse = fileClient.getFileById(post.getFileId()).getData();
            } catch (FeignException e) {
//                throw new AppException(ErrorCode.FILE_NOT_FOUND);
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

        ApiResponse<List<PostResponse>> response = ApiResponse.<List<PostResponse>>builder()
                .data(result)
                .meta(metaResponse)
                .build();

//        if (cache != null) {
//            cache.put(cacheKey, response);
//        }

        return response;
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
