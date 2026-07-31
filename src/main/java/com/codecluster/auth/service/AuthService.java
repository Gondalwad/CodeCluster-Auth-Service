package com.codecluster.auth.service;

import com.codecluster.auth.dto.request.RegisterRequest;
import com.codecluster.auth.dto.response.AuthResponse;
import com.codecluster.auth.dto.request.LoginRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}