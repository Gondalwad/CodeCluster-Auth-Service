package com.codecluster.auth.repository;

import com.codecluster.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    //Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("""
    SELECT DISTINCT u
    FROM User u
    LEFT JOIN FETCH u.userRoles ur
    LEFT JOIN FETCH ur.role
    WHERE u.username = :username
    """)
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
}