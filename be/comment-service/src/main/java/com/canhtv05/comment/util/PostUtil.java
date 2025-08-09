//package com.canhtv05.comment.util;
//
//import com.canhtv05.comment.dto.res.FileResponse;
//import com.canhtv05.comment.dto.res.UserProfileResponse;
//import com.canhtv05.comment.exception.AppException;
//import com.canhtv05.comment.exception.ErrorCode;
//import com.canhtv05.comment.repository.httpclient.FileClient;
//import com.canhtv05.comment.repository.httpclient.UserProfileClient;
//import feign.FeignException;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class PostUtil {
//
//    FileClient fileClient;
//    UserProfileClient userProfileClient;
//
//    public Map<String, FileResponse> getFileResponseMap(Page<Post> pageResponse) {
//        List<String> fileIds = pageResponse.getContent().stream()
//                .map(Post::getFileId)
//                .filter(Objects::nonNull)
//                .distinct()
//                .toList();
//
//        // tránh gọi feign nhiều lần
//        Map<String, FileResponse> fileMap = Collections.emptyMap();
//        try {
//            if (!fileIds.isEmpty()) {
//                var fileList = fileClient.getFilesByIds(fileIds).getData();
//                fileMap = fileList.stream()
//                        .collect(Collectors.toMap(FileResponse::getId, f -> f));
//            }
//        } catch (FeignException e) {
//            log.error(e.getMessage());
//        }
//        return fileMap;
//    }
//
//    public UserProfileResponse getUserProfileResponse(String userId) {
//        UserProfileResponse userProfile;
//        try {
//            userProfile = userProfileClient.getUserProfile(userId).getData();
//        } catch (FeignException _) {
//            throw new AppException(ErrorCode.USER_NOT_FOUND);
//        }
//
//        if (userProfile == null)
//            throw new AppException(ErrorCode.USER_NOT_FOUND);
//
//        return userProfile;
//    }
//
//    public List<String> extractHashtags(String content) {
//        List<String> hashtags = new ArrayList<>();
//
//        String[] words = content.split("\\s+");
//
//        for (int i = 0; i < words.length; i++) {
//            if (words[i].startsWith("#") && words[i].length() > 1) {
//                String hashtag = words[i].replaceAll("[^#\\w]", "");
//                hashtags.add(hashtag);
//            }
//        }
//        return hashtags;
//    }
//}
