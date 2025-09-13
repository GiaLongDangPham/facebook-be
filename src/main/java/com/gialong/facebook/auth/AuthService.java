package com.gialong.facebook.auth;

import com.gialong.facebook.auth.request.*;
import com.gialong.facebook.auth.response.AuthResponse;
import com.gialong.facebook.auth.token.Token;
import com.gialong.facebook.auth.token.TokenRepository;
import com.gialong.facebook.auth.token.TokenService;
import com.gialong.facebook.base.BaseRedisService;
import com.gialong.facebook.email.EmailService;
import com.gialong.facebook.email.request.Recipient;
import com.gialong.facebook.email.request.SendEmailRequest;
import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.user.*;
import com.gialong.facebook.userprofile.UserProfile;
import com.gialong.facebook.userprofile.UserProfileRepository;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final BaseRedisService baseRedisService;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    public UUID register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())
                || userProfileRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .role(Role.USER)
                .build();
        user = userRepository.save(user);

        // tạo profile cơ bản
        UserProfile profile = UserProfile.builder()
                .user(user)
                .username(request.getUsername())
                .fullName(request.getFullName())
                .build();
        userProfileRepository.save(profile);
        return user.getId();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        tokenService.addToken(user, accessToken, refreshToken);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userResponse(userMapper.toUserResponse(user))
                .build();
    }

    @Transactional
    public void logout(LogoutRequest request) {
        String accessToken = request.getAccessToken();
        tokenRepository.findByToken(accessToken).ifPresent(token -> {
            // set revoke
            token.setRevoked(true);
            tokenRepository.save(token);
        });

        long accessTokenExp = jwtService.extractTokenExpired(request.getAccessToken());
        if(accessTokenExp > 0) {
            try {
                String jwtId = SignedJWT.parse(request.getAccessToken()).getJWTClaimsSet().getJWTID();
                baseRedisService.set(jwtId, request.getAccessToken());
                baseRedisService.setTimeToLive(jwtId, accessTokenExp, TimeUnit.MILLISECONDS);
            } catch (ParseException e) {
                throw new AppException(ErrorCode.SIGN_OUT_FAILED);
            }
        }
    }

    @Transactional
    public void logoutAllDevices() {
        UUID userId = getMyInfo();
        List<Token> tokens = tokenRepository.findByUserId(userId);

        for (Token token : tokens) {
            // Blacklist access token
            long accessTokenExp = jwtService.extractTokenExpired(token.getToken());
            if(accessTokenExp > 0) {
                try {
                    String jwtId = SignedJWT.parse(token.getToken()).getJWTClaimsSet().getJWTID();
                    baseRedisService.set(jwtId, token.getToken());
                    baseRedisService.setTimeToLive(jwtId, accessTokenExp, TimeUnit.MILLISECONDS);
                } catch (ParseException e) {
                    throw new AppException(ErrorCode.SIGN_OUT_FAILED);
                }
            }
            // set revoke
            token.setRevoked(true);
            tokenRepository.save(token);
        }
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        Token token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_INVALID));

        // Kiểm tra expired
        if (token.getRefreshExpirationDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        User user = token.getUser();
        tokenRepository.delete(token);

        // Tạo access token mới
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Cập nhật token trong database
        tokenService.addToken(user, newAccessToken, newRefreshToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userResponse(userMapper.toUserResponse(user))
                .build();
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tạo reset token
        String resetToken = UUID.randomUUID().toString();
        long expiration = 15; // phút

        // Lưu vào Redis
        baseRedisService.set("RESET_" + resetToken, user.getEmail());
        baseRedisService.setTimeToLive("RESET_" + resetToken, expiration, TimeUnit.MINUTES);

        // TODO: Gửi email cho user kèm link reset-password
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .to(Recipient.builder()
                        .name(user.getProfile().getUsername()) // nếu có profile
                        .email(user.getEmail())
                        .build())
                .subject("Reset your password")
                .htmlContent(
                        "<h2>Password Reset Request</h2>" +
                                "<p>Xin chào " + user.getProfile().getUsername() + ",</p>" +
                                "<p>Bạn vừa yêu cầu reset mật khẩu. Hãy bấm vào link dưới đây để đặt lại mật khẩu:</p>" +
                                "<p><a href=\"" + resetLink + "\">Reset Password</a></p>" +
                                "<p>Link này chỉ có hiệu lực trong " + expiration + " phút.</p>"
                )
                .build();

        emailService.sendEmail(emailRequest);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = (String) baseRedisService.get("RESET_" + request.getToken());
        if (email == null) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Xoá token sau khi dùng
        baseRedisService.delete("RESET_" + request.getToken());
    }

    public UUID getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return currentUser.getId();
    }
}
