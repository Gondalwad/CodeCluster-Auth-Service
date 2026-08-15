package com.codecluster.auth.dto;

public class GenerateJwtDto {
    String username;
    String userRole;
    String userId;
    String instituteRole;
    String instituteId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInstituteRole() {
        return instituteRole;
    }

    public void setInstituteRole(String instituteRole) {
        this.instituteRole = instituteRole;
    }

    public String getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(String instituteId) {
        this.instituteId = instituteId;
    }
}
