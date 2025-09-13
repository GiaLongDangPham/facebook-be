package com.gialong.facebook.messageroommember;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRoomMemberRepository extends JpaRepository<MessageRoomMember, MessageRoomMemberKey> {
}