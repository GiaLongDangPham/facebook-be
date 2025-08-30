package com.gialong.facebook.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUserId(UUID userId);
    Optional<Token> findByToken(String token);
    Optional<Token> findByRefreshToken(String token);

    @Transactional
    void deleteByIsRevokedTrueOrExpirationDateBefore(LocalDateTime now);
}

