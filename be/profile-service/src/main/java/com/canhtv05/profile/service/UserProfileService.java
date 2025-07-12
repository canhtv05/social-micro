package com.canhtv05.profile.service;

import com.canhtv05.profile.dto.req.SendFriendRequest;
import com.canhtv05.profile.dto.req.UserProfileCreationRequest;
import com.canhtv05.profile.dto.res.FriendRequestResponse;
import com.canhtv05.profile.dto.res.UserProfileResponse;

public interface UserProfileService {

    UserProfileResponse create(UserProfileCreationRequest userProfileCreationRequest);

    FriendRequestResponse sendFriendRequest(SendFriendRequest request);
}
