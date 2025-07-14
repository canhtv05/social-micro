package com.canhtv05.profile.service.impl;

import com.canhtv05.profile.common.FriendRequestStatus;
import com.canhtv05.profile.dto.req.SendFriendRequest;
import com.canhtv05.profile.dto.req.UserProfileCreationRequest;
import com.canhtv05.profile.dto.res.FriendRequestResponse;
import com.canhtv05.profile.dto.res.UserProfileResponse;
import com.canhtv05.profile.entity.FriendRequest;
import com.canhtv05.profile.entity.UserProfile;
import com.canhtv05.profile.exception.AppException;
import com.canhtv05.profile.exception.ErrorCode;
import com.canhtv05.profile.mapper.FriendRequestMapper;
import com.canhtv05.profile.mapper.UserProfileMapper;
import com.canhtv05.profile.repository.UserProfileRepository;
import com.canhtv05.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileImplementation implements UserProfileService {

    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    FriendRequestMapper friendRequestMapper;

    @Override
    public UserProfileResponse create(UserProfileCreationRequest userProfileCreationRequest) {
        UserProfile userProfile = userProfileMapper.toUserProfileCreationRequest(userProfileCreationRequest);

        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(userProfile));
    }

    @Override
    public FriendRequestResponse sendFriendRequest(SendFriendRequest request) {
        UserProfile sender = userProfileRepository
                .findByUserId(request.getSenderUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserProfile receiver = userProfileRepository
                .findByUserId(request.getReceiverUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FriendRequest friendRequest = FriendRequest.builder()
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();

        sender.getFriendRequests().add(friendRequest);
        userProfileRepository.save(sender);

        return friendRequestMapper.toFriendRequestResponse(friendRequest);
    }
}
