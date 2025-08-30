package com.gialong.facebook.auth.token;

import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {
    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private final TokenRepository tokenRepository;

    @Transactional
    public void addToken(User user, String accessToken, String refreshToken) {
        // Tạo mới một token cho người dùng
        Token newToken = Token.builder()
                .user(user)
                .token(accessToken)
                .expirationDate(LocalDateTime.now().plusSeconds(expiration))
                .tokenType("Bearer")
                .refreshToken(refreshToken)
                .refreshExpirationDate(LocalDateTime.now().plusSeconds(refreshExpiration))
                .isRevoked(false)
                .build();
        tokenRepository.save(newToken);
    }

    public String findRefreshTokenByAccessToken(String accessToken) {
        Token token = tokenRepository.findByToken(accessToken)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_INVALID));
        return token.getRefreshToken();
    }

    @Scheduled(cron = "0 0 * * * *") // giây, phút, giờ, ngày, tháng, thứ
    public void cleanRevokedTokens() {
        tokenRepository.deleteByIsRevokedTrueOrExpirationDateBefore(LocalDateTime.now());
    }
}
