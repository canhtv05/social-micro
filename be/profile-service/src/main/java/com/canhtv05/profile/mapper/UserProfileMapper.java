package com.canhtv05.profile.mapper;

import com.canhtv05.profile.dto.req.UserProfileCreationRequest;
import com.canhtv05.profile.dto.req.UserProfileUpdateRequest;
import com.canhtv05.profile.dto.res.FriendRequestResponse;
import com.canhtv05.profile.dto.res.UserProfileResponse;
import com.canhtv05.profile.entity.FriendRequest;
import com.canhtv05.profile.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "friendRequests", ignore = true)
    @Mapping(target = "receivedFriendRequests", ignore = true)
    @Mapping(target = "friends", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "coverUrl", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followersCount", ignore = true)
    @Mapping(target = "following", ignore = true)
    @Mapping(target = "lastOnlineAt", ignore = true)
    @Mapping(target = "followingCount", ignore = true)
    @Mapping(target = "friendsListVisibility", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "profileVisibility", ignore = true)
    @Mapping(target = "postsVisibility", ignore = true)
    @Mapping(target = "socialLinks", ignore = true)
    UserProfile toUserProfileCreationRequest(UserProfileCreationRequest userProfileCreationRequest);

    @Mapping(target = "friendRequests", ignore = true)
    @Mapping(target = "receivedFriendRequests", ignore = true)
    @Mapping(target = "friends", ignore = true)
    @Mapping(target = "lastOnlineAt", ignore = true)
    @Mapping(target = "followingCount", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followersCount", ignore = true)
    @Mapping(target = "following", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    void toUserProfileUpdateRequest(UserProfileUpdateRequest request, @MappingTarget UserProfile userProfile);

    @Mapping(target = "mutualFriends", source = "friends")
    UserProfileResponse toUserProfileResponse(UserProfile userProfile);
}
