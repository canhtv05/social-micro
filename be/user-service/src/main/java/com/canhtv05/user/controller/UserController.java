package com.canhtv05.user.controller;

import com.canhtv05.user.dto.ApiResponse;
import com.canhtv05.user.dto.filter.UserFilter;
import com.canhtv05.user.dto.req.RefreshTokenRequest;
import com.canhtv05.user.dto.req.UserCreationRequest;
import com.canhtv05.user.dto.req.UserUpdateRequest;
import com.canhtv05.user.dto.res.UserResponse;
import com.canhtv05.user.service.UserService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @GetMapping("/internal/email/{email}")
    public ApiResponse<UserResponse> getUserByEmail(@PathVariable String email) {
        return ApiResponse.<UserResponse>builder()
                .message("success")
                .data(userService.getUserByEmail(email))
                .build();
    }

    @PutMapping("/internal/update/refresh-token")
    public ApiResponse<String> updateRefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        userService.updateRefreshToken(refreshTokenRequest);
        return ApiResponse.<String>builder()
                .message("success")
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.createUser(request))
                .build();
    }

    @PutMapping("/internal/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable(name = "userId") String userId,
            @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateUser(userId, request))
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<UserResponse>> getAllUsers(
            @ModelAttribute UserFilter filter,
            @RequestParam(required = false, defaultValue = "1", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "15", name = "size") Integer size) {

        return userService.getAllUsers(filter, page, size);
    }
}
