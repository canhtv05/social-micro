package com.canhtv05.profile.mapper;

import com.canhtv05.profile.dto.res.FriendRequestResponse;
import com.canhtv05.profile.entity.FriendRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendRequestMapper {

    @Mapping(source = "receiver.userId", target = "receiverUserId")
    @Mapping(source = "receiver.username", target = "receiverUsername")
    @Mapping(source = "receiver.avatarUrl", target = "receiverAvatarUrl")
    FriendRequestResponse toFriendRequestResponse(FriendRequest friendRequest);
}
