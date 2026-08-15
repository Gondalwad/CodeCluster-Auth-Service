package com.codecluster.auth.repository;

import com.codecluster.auth.entity.InstituteMember;
import com.codecluster.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InstituteMembersRepo extends JpaRepository<InstituteMember, UUID> {
    Optional<InstituteMember> findByUser(User user);
}
