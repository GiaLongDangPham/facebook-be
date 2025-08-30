package com.gialong.facebook.mention;

import com.gialong.facebook.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MentionRepository extends JpaRepository<Mention, UUID> {
    List<Mention> findByMentionedUser(User user);
}