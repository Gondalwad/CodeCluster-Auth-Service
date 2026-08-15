package com.codecluster.auth.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserResponse {

    private UUID id;

    private String email;

    private String name;

    private String role;

    private boolean isActive;

    private OffsetDateTime createdAt;
    private String username;

    public UserResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUsername(String username) {
        this.username=username;
    }

    public String getUsername() {
        return username;
    }
}