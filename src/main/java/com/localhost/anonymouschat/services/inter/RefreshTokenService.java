package com.localhost.anonymouschat.services.inter;

import com.localhost.anonymouschat.models.RefreshToken;
import com.localhost.anonymouschat.payload.request.RefreshTokenRequest;
import com.localhost.anonymouschat.payload.response.RefreshTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String userId);
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshTokenResponse generateNewToken(RefreshTokenRequest request);
    ResponseCookie generateRefreshTokenCookie(String token);
    String getRefreshTokenFromCookies(HttpServletRequest request);
    void deleteByToken(String token);
    ResponseCookie getCleanRefreshTokenCookie();
}
