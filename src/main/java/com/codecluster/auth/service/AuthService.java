package com.codecluster.auth.service;

import com.codecluster.auth.dto.request.RegisterRequest;
import com.codecluster.auth.dto.response.AuthResponse;
import com.codecluster.auth.dto.request.LoginRequest;

import com.codecluster.auth.dto.request.RefreshTokenRequest;
import com.codecluster.auth.dto.response.RefreshTokenResponse;

import com.codecluster.auth.dto.request.TokenValidateRequest;
import com.codecluster.auth.dto.response.TokenValidateResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(String token);

    TokenValidateResponse validateToken(TokenValidateRequest request);



}