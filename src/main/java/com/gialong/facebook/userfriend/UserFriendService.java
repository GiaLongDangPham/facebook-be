package com.gialong.facebook.userfriend;

import com.gialong.facebook.base.PageResponse;
import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserMapper;
import com.gialong.facebook.user.UserRepository;
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

    // Danh sách bạn bè (kể cả chưa accept)
    public PageResponse<UserFriendResponse> getFriends(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserFriend> userFriends = userFriendRepository
                .findByRequesterOrAddressee(user, user, pageable);

        List<UserFriendResponse> content = userFriends.stream()
                .map(uf -> toResponse(uf, user, uf.getStatus()))
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