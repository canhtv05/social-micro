package com.canhtv05.user.mapper;

import com.canhtv05.user.dto.req.UserCreationRequest;
import com.canhtv05.user.dto.req.UserProfileCreationRequest;
import com.canhtv05.user.dto.req.UserUpdateRequest;
import com.canhtv05.user.dto.res.UserResponse;
import com.canhtv05.user.enity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserResponse toUserResponse(User user);

    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toUserCreationRequest(UserCreationRequest userCreationRequest);

    @Mapping(target = "userId", ignore = true)
    UserProfileCreationRequest toUserProfileCreationRequest(UserCreationRequest request);

    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    void toUserProfileUpdateRequest(UserUpdateRequest request, @MappingTarget User user);
}
