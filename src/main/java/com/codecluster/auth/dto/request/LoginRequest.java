package com.codecluster.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String preferredId;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {
    }

    public String getPreferredId() {
        return preferredId;
    }

    public void setPreferredId(String preferredId) {
        this.preferredId = preferredId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}