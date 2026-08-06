package com.codecluster.auth.controller;

import com.codecluster.auth.dto.response.CurrentUserResponse;
import com.codecluster.auth.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.codecluster.auth.dto.request.UpdateProfileRequest;
import com.codecluster.auth.dto.response.MessageResponse;

import com.codecluster.auth.dto.request.ChangePasswordRequest;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(
        name = "Users",
        description = "User Profile APIs"
)
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @Operation(
            summary = "Current User",
            description = "Returns the profile of the currently authenticated user."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser() {

        CurrentUserResponse response =
                userService.getCurrentUser();

        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Update Profile",
            description = "Updates the authenticated user's name and email."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<MessageResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        MessageResponse response =
                userService.updateProfile(request);

        return ResponseEntity.ok(response);
    }



    @Operation(
            summary = "Change Password",
            description = "Changes the authenticated user's password."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/me/password")
    public ResponseEntity<MessageResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        MessageResponse response =
                userService.changePassword(request);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/test")
    public ResponseEntity<String> adminTest() {
        return ResponseEntity.ok("Welcome Admin");
    }
}