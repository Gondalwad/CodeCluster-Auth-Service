package com.codecluster.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role {

    public Role() {
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

//    @OneToMany(mappedBy = "role")
//    private List<RolePermission> rolePermissions;

//    public List<RolePermission> getRolePermissions() {
//        return rolePermissions;
//    }
//
//    public void setRolePermissions(List<RolePermission> rolePermissions) {
//        this.rolePermissions = rolePermissions;
//    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    @Id
    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "role")
    private List<UserRole> userRoles;

}