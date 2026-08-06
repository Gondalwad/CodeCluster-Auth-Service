package com.codecluster.auth.service;

import com.codecluster.auth.dto.response.CurrentUserResponse;

import com.codecluster.auth.dto.request.UpdateProfileRequest;
import com.codecluster.auth.dto.response.MessageResponse;

import com.codecluster.auth.dto.request.ChangePasswordRequest;

public interface UserService {

    CurrentUserResponse getCurrentUser();

    MessageResponse updateProfile(UpdateProfileRequest request);

    MessageResponse changePassword(ChangePasswordRequest request);



}