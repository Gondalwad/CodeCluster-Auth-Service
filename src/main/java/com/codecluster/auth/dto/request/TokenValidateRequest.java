package com.codecluster.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class TokenValidateRequest {

    @NotBlank
    @Schema(
            description="JWT Access Token",
            example="eyJhbGc..."
    )
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}