package com.canhtv05.user.service.impl;

import com.canhtv05.user.common.UserStatus;
import com.canhtv05.user.dto.ApiResponse;
import com.canhtv05.user.dto.MetaResponse;
import com.canhtv05.user.dto.PageResponse;
import com.canhtv05.user.dto.filter.UserFilter;
import com.canhtv05.user.dto.req.RefreshTokenRequest;
import com.canhtv05.user.dto.req.UserCreationRequest;
import com.canhtv05.user.dto.req.UserProfileCreationRequest;
import com.canhtv05.user.dto.res.UserResponse;
import com.canhtv05.user.enity.Role;
import com.canhtv05.user.enity.User;
import com.canhtv05.user.exception.AppException;
import com.canhtv05.user.exception.ErrorCode;
import com.canhtv05.user.mapper.RoleMapper;
import com.canhtv05.user.mapper.UserMapper;
import com.canhtv05.user.repository.RoleRepository;
import com.canhtv05.user.repository.UserRepository;
import com.canhtv05.user.repository.httpclient.UserProfileClient;
import com.canhtv05.user.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImplementation implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    RoleMapper roleMapper;
    UserProfileClient userProfileClient;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @Cacheable(value = "getUserByEmail", key = "#email")
    @Override
    public UserResponse getUserByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var roles = user.getRoles().stream()
                .map(roleMapper::toRoleResponse)
                .toList();
        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setRoles(roles);

        return userResponse;
    }

    @CacheEvict(value = "getUserByEmail", key = "#refreshTokenRequest.email")
    @Override
    public void updateRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        var user = userRepository.findByEmail(refreshTokenRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setRefreshToken(refreshTokenRequest.getRefreshToken());
        userRepository.save(user);
    }

    @CacheEvict(value = "getUserByEmail", allEntries = true)
    @Override
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EXISTS_BY_EMAIL);
        }

        User user = userMapper.toUserCreationRequest(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserStatus(UserStatus.ACTIVE);

        List<Role> roles = roleRepository.findAll();
        user.setRoles(roles);

        user = userRepository.save(user);

        UserProfileCreationRequest userProfileCreationRequest = userMapper.toUserProfileCreationRequest(request);
        try {
            userProfileCreationRequest.setUserId(UUID.fromString(user.getId()));
        } catch (IllegalArgumentException _) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        userProfileClient.createUserProfile(userProfileCreationRequest);

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ApiResponse<List<UserResponse>> getAllUsers(UserFilter filter, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Order.desc("createdAt")));

        Page<User> users = userRepository.getAllUsers(
                filter.getEmail(),
                filter.getUsername(),
                pageable);

        MetaResponse<Void> metaResponse = MetaResponse.<Void>builder()
                .page(PageResponse.builder()
                        .currentPage(page + 1)
                        .pageSize(size)
                        .totalElements(users.getTotalElements())
                        .totalPages(users.getTotalPages())
                        .build())
                .build();

        var result = users.getContent().stream()
                .map(userMapper::toUserResponse)
                .toList();

        result.forEach(userResponse -> userResponse.setRefreshToken(null));

        return ApiResponse.<List<UserResponse>>builder()
                .data(result)
                .meta(metaResponse)
                .build();
    }
}
