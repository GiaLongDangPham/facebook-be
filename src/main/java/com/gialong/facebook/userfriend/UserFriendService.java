package com.gialong.facebook.userfriend;

import com.gialong.facebook.base.PageResponse;
import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.notification.ActionEnum;
import com.gialong.facebook.notification.NotificationService;
import com.gialong.facebook.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserFriendService {

    private final UserFriendRepository userFriendRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    // Gửi lời mời kết bạn
    public UserFriendResponse sendFriendRequest(UUID currentUserId, UUID addresseeId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        User addressee = userRepository.findById(addresseeId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserFriend friendship = UserFriend.builder()
                .requester(currentUser)
                .addressee(addressee)
                .status(FriendshipStatus.PENDING)
                .build();

        notificationService.sendNotification(addressee, currentUser, null, null, ActionEnum.ADD_FRIEND);

        return this.toResponse(userFriendRepository.save(friendship), currentUser, FriendshipStatus.PENDING);
    }

    // Chấp nhận lời mời
    public UserFriendResponse acceptFriendRequest(UUID currentUserId, UUID requesterId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserFriend friendship = userFriendRepository.findByRequesterAndAddressee(requester, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.YOU_ARE_NOT_FRIENDS));

        friendship.setStatus(FriendshipStatus.ACCEPTED);

        notificationService.sendNotification(requester, currentUser, null, null, ActionEnum.ACCEPT_FRIEND);
        return this.toResponse(userFriendRepository.save(friendship), currentUser, FriendshipStatus.ACCEPTED);
    }

    public void cancelRequest(UUID addresseeId, UUID currentUserId) {
        var addressee = userRepository.findById(addresseeId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserFriend friendship = userFriendRepository.findByRequesterAndAddressee(currentUser, addressee)
                .orElseThrow(() -> new AppException(ErrorCode.YOU_ARE_NOT_FRIENDS));

        userFriendRepository.delete(friendship);
    }

    // Từ chối / hủy lời mời
    public void rejectFriendRequest(UUID requesterId, UUID currentUserId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserFriend friendship = userFriendRepository.findByRequesterAndAddressee(requester, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.YOU_ARE_NOT_FRIENDS));

        userFriendRepository.delete(friendship);
    }

    // Hủy kết bạn (khi đã ACCEPTED)
    public void unfriend(UUID otherUserId, UUID currentUserId) {
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userFriendRepository.findByRequesterAndAddressee(otherUser, currentUser)
                .ifPresent(userFriendRepository::delete);

        userFriendRepository.findByRequesterAndAddressee(currentUser, otherUser)
                .ifPresent(userFriendRepository::delete);
    }

    // Danh sách bạn bè (đã accept)
    public PageResponse<UserFriendResponse> getFriends(User user, int page, int size, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserFriend> userFriends;

        if (searchKeyword == null || searchKeyword.isBlank()) {
            userFriends = userFriendRepository
                    .findByRequesterOrAddresseeAndStatus(user, user, FriendshipStatus.ACCEPTED, pageable);
        } else {
            userFriends = userFriendRepository
                    .searchFriends(user, FriendshipStatus.ACCEPTED, searchKeyword, pageable);
        }

        List<UserFriendResponse> content = userFriends.stream()
                .map(uf -> toResponse(uf, user, FriendshipStatus.ACCEPTED))
                .toList();

        return PageResponse.<UserFriendResponse>builder()
                .content(content)
                .page(userFriends.getNumber())
                .size(userFriends.getSize())
                .totalElements(userFriends.getTotalElements())
                .totalPages(userFriends.getTotalPages())
                .last(userFriends.isLast())
                .build();
    }

    public PageResponse<UserFriendResponse> getFriendRequests(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserFriend> userFriends = userFriendRepository
                    .findByAddresseeAndStatus(user, FriendshipStatus.PENDING, pageable);


        var content = userFriends.stream()
                .map(uf -> toResponse(uf, user, FriendshipStatus.PENDING))
                .toList();

        return PageResponse.<UserFriendResponse>builder()
                .content(content)
                .page(userFriends.getNumber())
                .size(userFriends.getSize())
                .totalElements(userFriends.getTotalElements())
                .totalPages(userFriends.getTotalPages())
                .last(userFriends.isLast())
                .build();
    }

    public PageResponse<UserFriendResponse> getFriendSuggests(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return null;
    }

    public String getFriendStatus(UUID currentUserId, User otherUser) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserFriend userFriend = userFriendRepository.findByRequesterAndAddressee(currentUser, otherUser)
                .orElseGet(() -> userFriendRepository.findByRequesterAndAddressee(otherUser, currentUser).orElse(null));

        if (userFriend == null) {
            return "none";
        }
        else if (userFriend.getStatus() == FriendshipStatus.ACCEPTED) {
            return "accepted";
        }
        else if (userFriend.getStatus() == FriendshipStatus.PENDING) {
            if (userFriend.getRequester().getId().equals(currentUserId)) {
                return "pending";
            } else {
                return "waiting";
            }
        } else {
            return "none";
        }
    }

    public List<UserResponse> getMutualFriends(UUID userId, UUID targetId) {
        return userFriendRepository.countMutualFriends(userId, targetId).stream()
                .map(userMapper::toUserResponse).toList();
    }

    private UserFriendResponse toResponse(UserFriend userFriend, User currentUser, FriendshipStatus status) {
        User other = userFriend.getRequester().getId().equals(currentUser.getId())
                ? userFriend.getAddressee()
                : userFriend.getRequester();
        switch (status) {
            case ACCEPTED -> {
                return UserFriendResponse.builder()
                        .otherUser(userMapper.toUserResponse(other))
                        .status("accepted")
                        .build();
            }
            case PENDING -> {
                // Nếu currentUser là requester
                if (userFriend.getRequester().getId().equals(currentUser.getId())) {
                    return UserFriendResponse.builder()
                        .otherUser(userMapper.toUserResponse(other))
                        .status("pending")
                        .build();
                } else { // Nếu currentUser là addressee thì status là "WAITING"
                    return UserFriendResponse.builder()
                        .otherUser(userMapper.toUserResponse(other))
                        .status("waiting")
                        .build();
                }
            }
            default -> throw new AppException(ErrorCode.YOU_ARE_NOT_FRIENDS);
        }
    }



}