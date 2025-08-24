package com.gialong.facebook.auth.token;

import com.gialong.facebook.auth.JwtService;
import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final int MAX_TOKENS = 3;
    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private final TokenRepository tokenRepository;

//    @Transactional
//    public Token refreshToken(String refreshToken, User user) throws Exception{
//        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
//        if(existingToken == null) {
//            throw new DataNotFoundException("Refresh token does not exist");
//        }
//        if(existingToken.getRefreshExpirationDate().compareTo(LocalDateTime.now()) < 0){
//            tokenRepository.delete(existingToken);
//            throw new ExpiredTokenException("Refresh token is expired");
//        }
//        String token = jwtTokenUtil.generateToken(user);
//        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);
//        existingToken.setExpirationDate(expirationDateTime);
//        existingToken.setToken(token);
//        existingToken.setRefreshToken(UUID.randomUUID().toString());
//        existingToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
//        return existingToken;
//    }

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


//    @Transactional
//    public ResponseEntity<?> confirmToken(String token) throws IOException, WriterException {
//        Token confirmationToken = tokenRepository.findByToken(token);
//        if(confirmationToken == null) {
//            throw new IllegalStateException("Token not found");
//        }
//        if (confirmationToken.isRevoked()) {
//            throw new IllegalStateException("Token already used");
//        }
//
//        LocalDateTime expiredAt = confirmationToken.getExpirationDate();
//        if (expiredAt.isBefore(LocalDateTime.now())) {
//            throw new IllegalStateException("token expired");
//        }
//        confirmationToken.setRevoked(true);
//        String qrCodeText = confirmationToken.getUser().getId().toString();
//        String qrCodeUrl = userService.generateQRCodeAndUploadToFirebase(qrCodeText, 300, 300);
//        userService.saveQRcode(qrCodeUrl,confirmationToken.getUser().getId());
//        tokenRepository.save(confirmationToken);
//        userService.enableUser(confirmationToken.getUser().getId());
//        return ResponseEntity.ok("Confirmed");
//    }
}
