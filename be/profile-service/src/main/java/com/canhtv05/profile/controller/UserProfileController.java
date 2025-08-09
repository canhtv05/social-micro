package com.canhtv05.profile.controller;

import com.canhtv05.profile.dto.ApiResponse;
import com.canhtv05.profile.dto.req.AcceptFriendRequest;
import com.canhtv05.profile.dto.req.FollowRequest;
import com.canhtv05.profile.dto.req.RejectFriendRequest;
import com.canhtv05.profile.dto.req.SendFriendRequest;
import com.canhtv05.profile.dto.req.UserProfileCreationRequest;
import com.canhtv05.profile.dto.req.UserProfileUpdateRequest;
import com.canhtv05.profile.dto.res.FriendRequestResponse;
import com.canhtv05.profile.dto.res.UserProfileResponse;
import com.canhtv05.profile.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {

    UserProfileService userProfileService;

    @PostMapping("/internal/create")
    public ResponseEntity<ApiResponse<UserProfileResponse>> createUserProfile(
            @Valid @RequestBody UserProfileCreationRequest request) {

        var response = ApiResponse.<UserProfileResponse>builder()
                .data(userProfileService.create(request))
                .code(201)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserProfileResponse> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .data(userProfileService.updateProfile(userId, request))
                .build();
    }

    @GetMapping("/{userId}")
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

    @PostMapping("/request/accept")
    public ResponseEntity<ApiResponse<Void>> acceptFriendRequest(@Valid @RequestBody AcceptFriendRequest request) {
        var response = ApiResponse.<Void>builder()
                .data(userProfileService.acceptFriendRequest(request))
                .code(204)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/request/reject")
    public ResponseEntity<ApiResponse<Void>> rejectFriendRequest(@Valid @RequestBody RejectFriendRequest request) {
        var response = ApiResponse.<Void>builder()
                .data(userProfileService.rejectFriendRequest(request))
                .code(204)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/follow")
    public ResponseEntity<ApiResponse<Void>> followUser(@Valid @RequestBody FollowRequest request) {
        var response = ApiResponse.<Void>builder()
                .data(userProfileService.followAnUser(request))
                .code(204)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<ApiResponse<Void>> unFollowUser(@Valid @RequestBody FollowRequest request) {
        var response = ApiResponse.<Void>builder()
                .data(userProfileService.unFollowAnUser(request))
                .code(204)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
