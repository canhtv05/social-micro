package com.canhtv05.user.mapper;

import com.canhtv05.user.dto.req.UserCreationRequest;
import com.canhtv05.user.dto.req.UserProfileCreationRequest;
import com.canhtv05.user.dto.res.UserResponse;
import com.canhtv05.user.enity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    User toUserCreationRequest(UserCreationRequest userCreationRequest);

    @Mapping(target = "userId", ignore = true)
    UserProfileCreationRequest toUserProfileCreationRequest(UserCreationRequest request);
}
