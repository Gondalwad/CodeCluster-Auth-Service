package com.codecluster.auth.repository;

import com.codecluster.auth.entity.UserRole;
import com.codecluster.auth.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

}