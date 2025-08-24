package com.gialong.facebook.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUserId(UUID userId);
    Optional<Token> findByToken(String token);
    Optional<Token> findByRefreshToken(String token);
}

