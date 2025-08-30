package com.gialong.facebook.userfriend;

import com.gialong.facebook.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFriendRepository extends JpaRepository<UserFriend, UserFriendId> {

    // Tìm mối quan hệ giữa 2 user
    Optional<UserFriend> findByRequesterAndAddressee(User requester, User addressee);

    // Danh sách bạn bè (kể cả chưa accept) của user (dù requester hay addressee)
    Page<UserFriend> findByRequesterOrAddressee(User u1, User u2, Pageable pageable);
}