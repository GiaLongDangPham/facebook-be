package com.gialong.facebook.auth.token;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/tokens")
public class TokenController {
    private final TokenService tokenService;

    @GetMapping("/refresh-token/{accessToken}")
    public Map<String, String> getRefreshToken(@PathVariable String accessToken) {
        return Map.of("refreshToken", tokenService.findRefreshTokenByAccessToken(accessToken));
    }
}
