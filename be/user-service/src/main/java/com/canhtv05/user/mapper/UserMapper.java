package com.canhtv05.user.mapper;

import com.canhtv05.user.dto.res.UserResponse;
import com.canhtv05.user.enity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "password", ignore = true)
    UserResponse toUserResponse(User user);
}
