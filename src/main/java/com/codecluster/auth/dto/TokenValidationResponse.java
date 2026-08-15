package com.codecluster.auth.dto;

public class TokenValidationResponse {

    private String userId;
    private String username;
    private String role;
    private String instituteId;
    private String instituteRole;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(String instituteId) {
        this.instituteId = instituteId;
    }

    public String getInstituteRole() {
        return instituteRole;
    }

    public void setInstituteRole(String instituteRole) {
        this.instituteRole = instituteRole;
    }
}