package com.gialong.facebook.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientIdAndCreatedAtAfterOrderByUpdatedAtDesc(UUID recipientId, Instant createdAt);

    @Query("SELECT count(*) FROM Notification n " +
            "WHERE n.recipient.id = :recipientId " +
            "AND n.state = :state"
    )
    Long countUnseen(@Param("recipientId") UUID recipientId, @Param("state") StateEnum state);

    @Query("SELECT n FROM Notification n " +
            "WHERE n.targetId = :targetId " +
            "AND n.actionType = :actionType"
    )
    Optional<Notification> findByTargetIdAndActionType(
            @Param("targetId") UUID targetId,
            @Param("actionType") ActionEnum actionType
    );

    @Modifying
    @Query("UPDATE Notification n " +
            "SET n.state = :newState " +
            "WHERE n.targetId = :targetId  " +
            "AND n.actionType = :actionType")
    void updateNotificationState(@Param("targetId") UUID targetId, @Param("actionType") ActionEnum actionType, @Param("newState") StateEnum newState);


    @Modifying
    @Query("UPDATE Notification n " +
            "SET n.state = :seenState " +
            "WHERE n.recipient.id = :userId  " +
            "AND n.state = :unseenState ")
    void markAllSeen(@Param("userId") UUID userId, @Param("seenState") StateEnum seenState, @Param("unseenState") StateEnum unseenState);
}