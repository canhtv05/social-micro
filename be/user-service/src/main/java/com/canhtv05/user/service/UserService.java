package com.canhtv05.user.service;

import com.canhtv05.user.dto.req.RefreshTokenRequest;
import com.canhtv05.user.dto.req.UserCreationRequest;
import com.canhtv05.user.dto.res.UserResponse;

public interface UserService {

    UserResponse getUserByEmail(String email);

    void updateRefreshToken(RefreshTokenRequest request);

    UserResponse createUser(UserCreationRequest request);
}
