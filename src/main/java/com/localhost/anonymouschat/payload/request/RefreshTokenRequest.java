package com.localhost.anonymouschat.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class RefreshTokenRequest {
    @NotBlank(message = "refresh token is required")
    private String refreshToken;

}
