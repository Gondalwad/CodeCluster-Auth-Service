package com.codecluster.auth.dto.request;

//import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequest {


    @NotBlank(message = "Username or Email is required")
    @Schema(
            description = "Username or Email",
            example = "test999"
    )
    private String usernameOrEmail;

    //private String usernameOrEmail;



    @NotBlank(message = "Password is required")
    @Schema(
            description = "Account Password",
            example = "Password123"
    )
    private String password;

    //private String password;

    public LoginRequest() {
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}