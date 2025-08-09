package com.canhtv05.profile.service;

import com.canhtv05.profile.dto.req.AcceptFriendRequest;
import com.canhtv05.profile.dto.req.FollowRequest;
import com.canhtv05.profile.dto.req.RejectFriendRequest;
import com.canhtv05.profile.dto.req.SendFriendRequest;
import com.canhtv05.profile.dto.req.UserProfileCreationRequest;
import com.canhtv05.profile.dto.req.UserProfileUpdateRequest;
import com.canhtv05.profile.dto.res.FriendRequestResponse;
import com.canhtv05.profile.dto.res.UserProfileResponse;

import java.util.List;

public interface UserProfileService {

    UserProfileResponse create(UserProfileCreationRequest userProfileCreationRequest);

    FriendRequestResponse sendFriendRequest(SendFriendRequest request);

    UserProfileResponse getUserProfile(String userId);

    List<UserProfileResponse> getUserProfilesByIds(List<String> userIds);

    UserProfileResponse updateProfile(String userProfileId, UserProfileUpdateRequest request);

    Void acceptFriendRequest(AcceptFriendRequest request);

    Void rejectFriendRequest(RejectFriendRequest request);

    Void followAnUser(FollowRequest request);

    Void unFollowAnUser(FollowRequest request);
}
