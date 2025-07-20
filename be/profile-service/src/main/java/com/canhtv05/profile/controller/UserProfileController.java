package com.canhtv05.profile.controller;

import com.canhtv05.profile.dto.ApiResponse;
import com.canhtv05.profile.dto.req.SendFriendRequest;
import com.canhtv05.profile.dto.req.UserProfileCreationRequest;
import com.canhtv05.profile.dto.res.FriendRequestResponse;
import com.canhtv05.profile.dto.res.UserProfileResponse;
import com.canhtv05.profile.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {

    UserProfileService userProfileService;

    @PostMapping("/internal/create")
    public ApiResponse<UserProfileResponse> createUserProfile(@Valid @RequestBody UserProfileCreationRequest request) {

        return ApiResponse.<UserProfileResponse>builder()
                .data(userProfileService.create(request))
                .build();
    }

    @GetMapping("/internal/users/{userId}")
    public ApiResponse<UserProfileResponse> getUserProfile(@PathVariable(name = "userId") String userId) {

        return ApiResponse.<UserProfileResponse>builder()
                .data(userProfileService.getUserProfile(userId))
                .build();
    }

    @GetMapping("/internal/users")
    public ApiResponse<List<UserProfileResponse>> getUserProfilesByIds(@RequestParam("ids") List<String> userIds) {
        List<UserProfileResponse> users = userProfileService.getUserProfilesByIds(userIds);
        return ApiResponse.<List<UserProfileResponse>>builder()
                .data(users)
                .build();
    }

    @PostMapping("/request")
    public ApiResponse<FriendRequestResponse> sendFriendRequest(@Valid @RequestBody SendFriendRequest request) {

        return ApiResponse.<FriendRequestResponse>builder()
                .data(userProfileService.sendFriendRequest(request))
                .build();
    }
}
