package com.codecluster.auth.service.impl;

import com.codecluster.auth.dto.response.CurrentUserResponse;
import com.codecluster.auth.entity.Role;
import com.codecluster.auth.entity.User;
import com.codecluster.auth.entity.UserStatus;
import com.codecluster.auth.exception.UserNotFoundException;
import com.codecluster.auth.repository.UserRepository;
import com.codecluster.auth.security.CustomUserDetails;
import com.codecluster.auth.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.codecluster.auth.dto.request.UpdateProfileRequest;
import com.codecluster.auth.dto.response.MessageResponse;

import com.codecluster.auth.dto.request.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CurrentUserResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Role role = user.getUserRoles()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("User has no role"))
                .getRole();

        CurrentUserResponse response = new CurrentUserResponse();

        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(role.getRoleName());
        response.setActive(user.getStatus() == UserStatus.active);
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }

    @Override
    public MessageResponse updateProfile(UpdateProfileRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsernameWithRoles(userDetails.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // Check if email is changing
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException("Email already exists");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        return new MessageResponse("Profile updated successfully");
    }

    @Override
    public MessageResponse changePassword(ChangePasswordRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsernameWithRoles(userDetails.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash())) {

            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        return new MessageResponse("Password changed successfully");
    }
}