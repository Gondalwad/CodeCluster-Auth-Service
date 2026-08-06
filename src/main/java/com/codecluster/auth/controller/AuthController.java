package com.codecluster.auth.controller;

import com.codecluster.auth.dto.request.RegisterRequest;
import com.codecluster.auth.dto.response.AuthResponse;
import com.codecluster.auth.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.codecluster.auth.dto.request.LoginRequest;

import com.codecluster.auth.dto.request.RefreshTokenRequest;
import com.codecluster.auth.dto.response.RefreshTokenResponse;

import com.codecluster.auth.dto.request.TokenValidateRequest;
import com.codecluster.auth.dto.response.TokenValidateResponse;
//import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Authentication",
        description = "Authentication APIs"
)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @Operation(
            summary = "Register User",
            description = "Creates a new user account."
    )

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Email or Username already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    //Login endpoint


    @Operation(
            summary = "Login",
            description = "Authenticates user and returns JWT access and refresh tokens."
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }


    //refreshing tokens


    @Operation(
            summary = "Login",
            description = "Authenticates user and returns JWT access and refresh tokens."
    )
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        RefreshTokenResponse response =
                authService.refreshToken(request);

        return ResponseEntity.ok(response);
    }

    //logout


    @Operation(
            summary = "Logout",
            description = "Blacklists the current JWT access token."
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String header) {

        authService.logout(header);

        return ResponseEntity.ok("Logout Successful");
    }

    //validate


    @Operation(
            summary = "Validate Token",
            description = "Checks whether the supplied JWT is valid."
    )
    @PostMapping("/validate")
    public ResponseEntity<TokenValidateResponse> validateToken(
            @Valid @RequestBody TokenValidateRequest request) {

        TokenValidateResponse response =
                authService.validateToken(request);

        return ResponseEntity.ok(response);
    }
}