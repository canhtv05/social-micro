package com.canhtv05.profile.mapper;

import com.canhtv05.profile.dto.req.UserProfileCreationRequest;
import com.canhtv05.profile.dto.res.UserProfileResponse;
import com.canhtv05.profile.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "friends", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserProfile toUserProfileCreationRequest(UserProfileCreationRequest userProfileCreationRequest);

    @Mapping(target = "mutualFriends", source = "friends")
    UserProfileResponse toUserProfileResponse(UserProfile userProfile);
}
