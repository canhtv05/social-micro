package com.canhtv05.user.service.impl;

import com.canhtv05.user.dto.req.RefreshTokenRequest;
import com.canhtv05.user.dto.res.UserResponse;
import com.canhtv05.user.exception.AppException;
import com.canhtv05.user.exception.ErrorCode;
import com.canhtv05.user.mapper.UserMapper;
import com.canhtv05.user.repository.UserRepository;
import com.canhtv05.user.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImplementation implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;

    @Override
    public UserResponse getUserByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    @Override
    public void updateRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        var user = userRepository.findByEmail(refreshTokenRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setRefreshToken(refreshTokenRequest.getRefreshToken());
        userRepository.save(user);
    }
}
