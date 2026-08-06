package com.codecluster.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    @Schema(
            description="JWT Refresh Token",
            example="eyJhbGciOiJIUzUxMiJ9..."
    )
    private String refreshToken;

    public RefreshTokenRequest() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}