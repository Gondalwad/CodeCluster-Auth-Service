package com.codecluster.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class UpdateProfileRequest {

    @NotBlank(message = "Name is required")
    @Schema(
            description="Full Name",
            example="John Doe"
    )
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    @Schema(
            description="User Email",
            example="john@gmail.com"
    )
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}