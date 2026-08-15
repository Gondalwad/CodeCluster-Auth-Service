package com.codecluster.auth.repository;

import com.codecluster.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findUsersByEmail(String email);

    Optional<User> findUserByUsername(String username);
}