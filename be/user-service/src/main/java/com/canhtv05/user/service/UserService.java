package com.canhtv05.user.service;

import com.canhtv05.user.dto.ApiResponse;
import com.canhtv05.user.dto.filter.UserFilter;
import com.canhtv05.user.dto.req.RefreshTokenRequest;
import com.canhtv05.user.dto.req.UserCreationRequest;
import com.canhtv05.user.dto.res.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getUserByEmail(String email);

    void updateRefreshToken(RefreshTokenRequest request);

    UserResponse createUser(UserCreationRequest request);

    ApiResponse<List<UserResponse>> getAllUsers(UserFilter filter, Integer page, Integer size);
}
