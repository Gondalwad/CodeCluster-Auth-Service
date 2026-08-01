package com.codecluster.auth.entity;

import jakarta.persistence.*;

import java.util.List;

import java.time.OffsetDateTime;
import java.util.UUID;
//import org.hibernate.annotations.JdbcTypeCode;
//import org.hibernate.type.SqlTypes;

//import org.hibernate.annotations.JdbcType;
//import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import com.codecluster.auth.converter.UserStatusConverter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "users")
public class User {
    //fields
    public User() {
    }

        public UUID getUserId () {
            return userId;
        }

        public void setUserId (UUID userId){
            this.userId = userId;
        }

        public String getName () {
            return name;
        }

        public void setName (String name){
            this.name = name;
        }

        public String getUsername () {
            return username;
        }

        public void setUsername (String username){
            this.username = username;
        }

        public String getEmail () {
            return email;
        }

        public void setEmail (String email){
            this.email = email;
        }

        public String getPasswordHash () {
            return passwordHash;
        }

        public void setPasswordHash (String passwordHash){
            this.passwordHash = passwordHash;
        }

        public OffsetDateTime getCreatedAt () {
            return createdAt;
        }

        public void setCreatedAt (OffsetDateTime createdAt){
            this.createdAt = createdAt;
        }

        public UserStatus getStatus () {
            return status;
        }

        public void setStatus (UserStatus status){
            this.status = status;
        }

        public OffsetDateTime getUpdatedAt () {
            return updatedAt;
        }

        public void setUpdatedAt (OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

         public List<UserRole> getUserRoles() {
        return userRoles;
        }

        public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
        }

//fields

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    //@Enumerated(EnumType.STRING)
    //@JdbcType(PostgreSQLEnumJdbcType.class)
//    @Column(name = "status")
//    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false, columnDefinition = "user_status")
    private UserStatus status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles;


}




