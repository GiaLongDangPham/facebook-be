package com.gialong.facebook.auth.token;

import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final int MAX_TOKENS = 3;
    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private final TokenRepository tokenRepository;

    @Transactional
    public void addToken(User user, String accessToken, String refreshToken) {
        List<Token> userTokens = tokenRepository.findByUserId(user.getId());
        int tokenCount = userTokens.size();
        // Số lượng token vượt quá giới hạn, xóa một token cũ
        if (tokenCount >= MAX_TOKENS) {
            Token tokenToDelete = userTokens.stream().min(Comparator.comparing(Token::getExpirationDate))
                    .orElseThrow();
            tokenRepository.delete(tokenToDelete);
        }
        // Tạo mới một token cho người dùng
        Token newToken = Token.builder()
                .user(user)
                .token(accessToken)
                .expirationDate(LocalDateTime.now().plusSeconds(expiration))
                .tokenType("Bearer")
                .refreshToken(refreshToken)
                .refreshExpirationDate(LocalDateTime.now().plusSeconds(refreshExpiration))
                .build();
        tokenRepository.save(newToken);
    }

    public String findRefreshTokenByAccessToken(String accessToken) {
        Token token = tokenRepository.findByToken(accessToken)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_INVALID));
        return token.getRefreshToken();
    }
}
