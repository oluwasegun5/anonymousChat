package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.enums.TokenType;
import com.localhost.anonymouschat.exception.TokenException;
import com.localhost.anonymouschat.models.RefreshToken;
import com.localhost.anonymouschat.models.User;
import com.localhost.anonymouschat.payload.request.RefreshTokenRequest;
import com.localhost.anonymouschat.payload.response.RefreshTokenResponse;
import com.localhost.anonymouschat.repositories.RefreshTokenRepository;
import com.localhost.anonymouschat.repositories.UserRepository;
import com.localhost.anonymouschat.services.inter.JwtService;
import com.localhost.anonymouschat.services.inter.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    @Value("${application.security.jwt.cookie-name}")
    private String refreshTokenName;
    @Override
    public RefreshToken createRefreshToken(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        RefreshToken refreshToken;
        if(refreshTokenRepository.existsById(userId)) {
            refreshToken = refreshTokenRepository.findById(userId).orElseThrow(() -> new RuntimeException("Refresh token not found"));
            refreshToken.setUser(user);
            refreshToken.setRevoked(false);
            refreshToken.setToken(Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()));
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        }else {
            refreshToken = RefreshToken.builder()
                    .revoked(false)
                    .id(userId)
                    .user(user)
                    .token(Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()))
                    .expiryDate(Instant.now().plusMillis(refreshExpiration))
                    .build();
        }

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token == null){
            log.error("Token is null");
            throw new TokenException(null, "Token is null");
        }
        if(token.getExpiryDate().compareTo(Instant.now()) < 0 ){
            refreshTokenRepository.delete(token);
            throw new TokenException(token.getToken(), "Refresh token was expired. Please make a new authentication request");
        }
        return token;
    }

//    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshTokenResponse generateNewToken(RefreshTokenRequest request) {
        User user = refreshTokenRepository.findByToken(request.getRefreshToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .orElseThrow(() -> new TokenException(request.getRefreshToken(),"Refresh token does not exist"));

        String token = jwtService.generateToken(user);
        return RefreshTokenResponse.builder()
                .accessToken(token)
                .refreshToken(request.getRefreshToken())
                .tokenType(TokenType.BEARER.name())
                .build();
    }

    @Override
    public ResponseCookie generateRefreshTokenCookie(String token) {
        return ResponseCookie.from(refreshTokenName, token)
                .path("/")
                .maxAge(refreshExpiration/1000) // 15 days in seconds
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    @Override
    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, refreshTokenName);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return "";
        }
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from(refreshTokenName, "")
                .path("/")
                .build();
    }
}
