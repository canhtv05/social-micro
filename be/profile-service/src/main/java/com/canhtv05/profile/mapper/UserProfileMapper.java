package com.canhtv05.profile.mapper;

import com.canhtv05.profile.dto.req.UserProfileCreationRequest;
import com.canhtv05.profile.dto.res.UserProfileResponse;
import com.canhtv05.profile.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "friendRequests", ignore = true)
    @Mapping(target = "receivedFriendRequests", ignore = true)
    @Mapping(target = "friends", ignore = true)
    UserProfile toUserProfileCreationRequest(UserProfileCreationRequest userProfileCreationRequest);

    @Mapping(target = "mutualFriends", source = "friends")
    UserProfileResponse toUserProfileResponse(UserProfile userProfile);
}
