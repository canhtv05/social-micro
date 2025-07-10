package com.canhtv05.user.controller;

import com.canhtv05.user.dto.ApiResponse;
import com.canhtv05.user.dto.req.RefreshTokenRequest;
import com.canhtv05.user.dto.res.UserResponse;
import com.canhtv05.user.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @GetMapping("/email/{email}")
    public ApiResponse<UserResponse> getUserByEmail(@PathVariable String email) {
        return ApiResponse.<UserResponse>builder()
                .message("success")
                .data(userService.getUserByEmail(email))
                .build();
    }

    @PutMapping("/update/refresh-token")
    public ApiResponse<?> updateRefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        userService.updateRefreshToken(refreshTokenRequest);
        return ApiResponse.builder()
                .message("success")
                .build();
    }
}
