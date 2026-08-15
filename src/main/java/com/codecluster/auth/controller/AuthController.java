package com.codecluster.auth.controller;

import com.codecluster.auth.dto.TokenValidationResponse;
import com.codecluster.auth.dto.request.RegisterRequest;
import com.codecluster.auth.dto.response.AuthResponse;
import com.codecluster.auth.dto.response.UserResponse;
import com.codecluster.auth.service.AuthService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.codecluster.auth.dto.request.LoginRequest;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * This controller creates the account of user task included, hashing the password and save to db.
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        UserResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    //Login endpoint

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Extracts claims from jwt andreturns
     * Input String Authorization : Containing JWT
     * Returns JwtClaims TokenValidationResponse Object :
     *     String userId;
     *     String username;
     *     String role;
     *     String instituteId; - may be null
     *     String instituteRole; - may be null
     */
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        return ResponseEntity.ok(authService.validateToken(authorization));
    }
}