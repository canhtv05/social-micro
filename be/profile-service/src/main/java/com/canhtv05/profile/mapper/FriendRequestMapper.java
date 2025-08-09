package com.canhtv05.profile.mapper;

import com.canhtv05.profile.dto.res.FriendRequestResponse;
import com.canhtv05.profile.entity.FriendRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendRequestMapper {

    @Mapping(target = "senderAvatarUrl", source = "sender.avatarUrl")
    @Mapping(target = "senderUserId", source = "sender.userId")
    @Mapping(target = "senderUsername", source = "sender.username")

    @Mapping(target = "receiverAvatarUrl", source = "receiver.avatarUrl")
    @Mapping(target = "receiverUserId", source = "receiver.userId")
    @Mapping(target = "receiverUsername", source = "receiver.username")
    FriendRequestResponse toFriendRequestResponse(FriendRequest request);
}
