package com.gialong.facebook.userfriend;

import com.gialong.facebook.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserFriendRepository extends JpaRepository<UserFriend, UserFriendId> {

    // Tìm mối quan hệ giữa 2 user
    Optional<UserFriend> findByRequesterAndAddressee(User requester, User addressee);

    Page<UserFriend> findByRequesterOrAddresseeAndStatus(User requester, User addressee, FriendshipStatus status,
                                                         Pageable pageable);

    Page<UserFriend> findByAddresseeAndStatus(User addressee, FriendshipStatus status, Pageable pageable);

    /*
    Tìm kiếm bạn bè theo từ khóa
     */
    @Query("""
        SELECT uf FROM UserFriend uf
        WHERE (
              (uf.requester = :user)
              AND uf.status = :status
              AND (
                    uf.addressee.profile.username LIKE %:keyword% OR
                    uf.addressee.profile.fullName LIKE %:keyword%
              )
          )
          OR (
              (uf.addressee = :user)
              AND uf.status = :status
              AND (
                uf.requester.profile.username LIKE %:keyword% OR
                uf.requester.profile.fullName LIKE %:keyword%
              )
           )
        """)
    Page<UserFriend> searchFriends(
            @Param("user") User user,
            @Param("status") FriendshipStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);

    /*
       bạn chung giữa 2 user
     */
    @Query("""
        SELECT u
        FROM User u
        WHERE u.id IN (
            SELECT CASE
                     WHEN f.requester.id = :userId THEN f.addressee.id
                     ELSE f.requester.id
                   END
            FROM UserFriend f
            WHERE f.status = 'ACCEPTED'
              AND (
                (f.requester.id = :userId AND f.addressee.id IN (
                  SELECT CASE WHEN ff.requester.id = :targetId THEN ff.addressee.id ELSE ff.requester.id END
                  FROM UserFriend ff
                  WHERE (ff.requester.id = :targetId OR ff.addressee.id = :targetId)
                    AND ff.status = 'ACCEPTED'
                ))
                OR
                (f.addressee.id = :userId AND f.requester.id IN (
                  SELECT CASE WHEN ff.requester.id = :targetId THEN ff.addressee.id ELSE ff.requester.id END
                  FROM UserFriend ff
                  WHERE (ff.requester.id = :targetId OR ff.addressee.id = :targetId)
                    AND ff.status = 'ACCEPTED'
                ))
              )
        )
    """)
    List<User> countMutualFriends(@Param("userId") UUID userId, @Param("targetId") UUID targetId);

    /*
    Lấy tất cả bạn đã accept của 1 user
     */
    @Query("""
        SELECT CASE
                 WHEN uf.requester.id = :userId THEN uf.addressee.id
                 ELSE uf.requester.id
               END
        FROM UserFriend uf
        WHERE (uf.requester.id = :userId OR uf.addressee.id = :userId)
          AND uf.status = 'ACCEPTED'
        """)
    List<UUID> findAllFriends(@Param("userId") UUID userId);
}