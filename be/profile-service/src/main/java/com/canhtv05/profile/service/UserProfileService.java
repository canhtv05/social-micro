package com.canhtv05.profile.service;

import com.canhtv05.profile.dto.req.SendFriendRequest;
import com.canhtv05.profile.dto.req.UserProfileCreationRequest;
import com.canhtv05.profile.dto.res.FriendRequestResponse;
import com.canhtv05.profile.dto.res.UserProfileResponse;

import java.util.List;

public interface UserProfileService {

    UserProfileResponse create(UserProfileCreationRequest userProfileCreationRequest);

    FriendRequestResponse sendFriendRequest(SendFriendRequest request);

    UserProfileResponse getUserProfile(String userId);

    List<UserProfileResponse> getUserProfilesByIds(List<String> userIds);
}
