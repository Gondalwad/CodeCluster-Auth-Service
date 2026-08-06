package com.codecluster.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    @Schema(
            description="Current Password",
            example="Password123"
    )
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Schema(
            description="New Password",
            example="Password456"
    )
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}