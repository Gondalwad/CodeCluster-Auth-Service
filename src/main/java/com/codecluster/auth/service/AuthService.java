package com.codecluster.auth.service;

import com.codecluster.auth.dto.request.RegisterRequest;
import com.codecluster.auth.dto.response.AuthResponse;
import com.codecluster.auth.dto.request.LoginRequest;
import com.codecluster.auth.dto.response.UserResponse;
import com.codecluster.auth.dto.TokenValidationResponse;
public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    TokenValidationResponse validateToken(String authorization);
}