package com.canhtv05.profile.service.impl;

import com.canhtv05.profile.common.FriendRequestStatus;
import com.canhtv05.profile.dto.req.AcceptFriendRequest;
import com.canhtv05.profile.dto.req.FollowRequest;
import com.canhtv05.profile.dto.req.RejectFriendRequest;
import com.canhtv05.profile.dto.req.SendFriendRequest;
import com.canhtv05.profile.dto.req.UserProfileCreationRequest;
import com.canhtv05.profile.dto.req.UserProfileUpdateRequest;
import com.canhtv05.profile.dto.req.UserUpdateRequest;
import com.canhtv05.profile.dto.res.FriendRequestResponse;
import com.canhtv05.profile.dto.res.UserProfileResponse;
import com.canhtv05.profile.entity.FriendRequest;
import com.canhtv05.profile.entity.UserProfile;
import com.canhtv05.profile.exception.AppException;
import com.canhtv05.profile.exception.ErrorCode;
import com.canhtv05.profile.mapper.FriendRequestMapper;
import com.canhtv05.profile.mapper.UserProfileMapper;
import com.canhtv05.profile.repository.FriendRequestRepository;
import com.canhtv05.profile.repository.UserProfileRepository;
import com.canhtv05.profile.repository.httpclient.UserClient;
import com.canhtv05.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileImplementation implements UserProfileService {

        UserProfileRepository userProfileRepository;
        UserProfileMapper userProfileMapper;
        FriendRequestMapper friendRequestMapper;
        FriendRequestRepository friendRequestRepository;
        UserClient userClient;

        @Override
        public UserProfileResponse create(UserProfileCreationRequest userProfileCreationRequest) {
                UserProfile userProfile = userProfileMapper.toUserProfileCreationRequest(userProfileCreationRequest);

                return userProfileMapper.toUserProfileResponse(userProfileRepository.save(userProfile));
        }

        @Override
        public UserProfileResponse updateProfile(String userProfileId, UserProfileUpdateRequest request) {
                UserProfile userProfile = getUserProfileOrThrow(userProfileId);

                userProfileMapper.toUserProfileUpdateRequest(request, userProfile);
                var response = userProfileMapper.toUserProfileResponse(userProfileRepository.save(userProfile));

                userClient.updateUser(response.getUserId(), UserUpdateRequest.builder()
                                .avatarUrl(response.getAvatarUrl())
                                .city(response.getCity())
                                .dob(response.getDob())
                                .gender(response.getGender())
                                .username(response.getUsername())
                                .build());
                return response;
        }

        @Override
        public FriendRequestResponse sendFriendRequest(SendFriendRequest request) {
                if (request.getSenderUserId().equals(request.getReceiverUserId())) {
                        throw new AppException(ErrorCode.INVALID_OPERATION);
                }

                UserProfile sender = getUserProfileOrThrow(request.getSenderUserId());

                UserProfile receiver = getUserProfileOrThrow(request.getReceiverUserId());

                boolean alreadyFriend = sender.getFriends().stream()
                                .anyMatch(u -> u.getUserId().equals(receiver.getUserId()));

                if (alreadyFriend)
                        throw new AppException(ErrorCode.ALREADY_FRIENDS);

                boolean exists = friendRequestRepository.existsPendingBetween(sender.getUserId(), receiver.getUserId());
                if (exists)
                        throw new AppException(ErrorCode.FRIEND_REQUEST_ALREADY_SENT);

                boolean existsReverse = friendRequestRepository.existsPendingBetween(receiver.getUserId(),
                                sender.getUserId());
                if (existsReverse) {
                        throw new AppException(ErrorCode.FRIEND_REQUEST_ALREADY_RECEIVED);
                }

                FriendRequest friendRequest = FriendRequest.builder()
                                .receiver(receiver)
                                .sender(sender)
                                .status(FriendRequestStatus.PENDING)
                                .build();

                friendRequest.setReceiver(receiver);
                friendRequest.setSender(sender);
                FriendRequest save = friendRequestRepository.save(friendRequest);

                sender.getFriendRequests().add(friendRequest);
                receiver.getReceivedFriendRequests().add(save);

                userProfileRepository.save(sender);
                userProfileRepository.save(receiver);
                return friendRequestMapper.toFriendRequestResponse(save);
        }

        @Override
        public Void acceptFriendRequest(AcceptFriendRequest request) {
                UserProfile receiver = getUserProfileOrThrow(request.getUserId());

                UUID requestId = UUID.fromString(request.getRequestId());

                FriendRequest friendRequest = friendRequestRepository.findByIdWithSenderAndReceiver(requestId)
                                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

                if (friendRequest.getStatus() != FriendRequestStatus.PENDING) {
                        throw new AppException(ErrorCode.INVALID_OPERATION);
                }

                UserProfile sender = friendRequest.getSender();

                boolean alreadyFriend = receiver.getFriends().stream()
                                .anyMatch(f -> f.getUserId().equals(sender.getUserId()));
                if (alreadyFriend) {
                        throw new AppException(ErrorCode.ALREADY_FRIENDS);
                }

                friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
                friendRequestRepository.save(friendRequest);

                receiver.getFriends().add(sender);
                sender.getFriends().add(receiver);

                userProfileRepository.save(receiver);
                userProfileRepository.save(sender);

                return null;
        }

        @Override
        public Void rejectFriendRequest(RejectFriendRequest request) {
                UserProfile receiver = getUserProfileOrThrow(request.getUserId());

                FriendRequest friendRequest = receiver.getReceivedFriendRequests().stream()
                                .filter(fr -> fr.getId().toString().equals(request.getRequestId()))
                                .findFirst()
                                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

                if (friendRequest.getStatus() != FriendRequestStatus.PENDING) {
                        throw new AppException(ErrorCode.INVALID_OPERATION);
                }

                friendRequest.setStatus(FriendRequestStatus.REJECTED);
                friendRequestRepository.save(friendRequest);

                return null;
        }

        @Override
        public UserProfileResponse getUserProfile(String userId) {
                UserProfile userProfile = userProfileRepository
                                .findByUserId(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                return userProfileMapper.toUserProfileResponse(userProfile);
        }

        @Override
        public List<UserProfileResponse> getUserProfilesByIds(List<String> userIds) {
                List<UserProfile> getUserProfiles = userIds.stream()
                                .map(userId -> userProfileRepository
                                                .findByUserId(userId)
                                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)))
                                .toList();

                return getUserProfiles.stream().map(userProfileMapper::toUserProfileResponse).toList();
        }

        @Override
        public Void followAnUser(FollowRequest request) {
                if (request.getFollowerId().equals(request.getUserTargetId())) {
                        throw new AppException(ErrorCode.INVALID_OPERATION);
                }

                UserProfile follower = getUserProfileOrThrow(request.getFollowerId());

                UserProfile userTarget = getUserProfileOrThrow(request.getUserTargetId());

                if (follower.getFollowing().contains(userTarget)) {
                        throw new AppException(ErrorCode.ALREADY_FOLLOWING);
                }

                follower.getFollowing().add(userTarget);
                follower.setFollowingCount(Math.max(follower.getFollowingCount() + 1L, 0L));

                userTarget.getFollowers().add(follower);
                userTarget.setFollowersCount(Math.max(userTarget.getFollowersCount() + 1L, 0L));

                userProfileRepository.save(follower);
                userProfileRepository.save(userTarget);

                return null;
        }

        @Override
        public Void unFollowAnUser(FollowRequest request) {
                if (request.getFollowerId().equals(request.getUserTargetId())) {
                        throw new AppException(ErrorCode.INVALID_OPERATION);
                }

                UserProfile follower = getUserProfileOrThrow(request.getFollowerId());

                UserProfile userTarget = getUserProfileOrThrow(request.getUserTargetId());

                if (!follower.getFollowing().contains(userTarget)) {
                        throw new AppException(ErrorCode.NOT_FOLLOWING);
                }

                follower.getFollowing().remove(userTarget);
                follower.setFollowingCount(Math.max(follower.getFollowingCount() - 1L, 0L));
                userTarget.getFollowers().remove(follower);
                userTarget.setFollowersCount(Math.max(userTarget.getFollowersCount() - 1L, 0L));

                userProfileRepository.save(follower);
                userProfileRepository.save(userTarget);

                return null;
        }

        private UserProfile getUserProfileOrThrow(String userId) {
                return userProfileRepository.findByUserId(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }
}
