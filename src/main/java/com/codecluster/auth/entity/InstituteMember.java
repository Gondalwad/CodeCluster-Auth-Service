package com.codecluster.auth.entity;

import com.codecluster.auth.enums.InstituteMemberRole;
import com.codecluster.auth.enums.UserStatus;
import com.thoughtworks.xstream.converters.enums.EnumToStringConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "institute_members")
public class InstituteMember {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "member_id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "institute_id", nullable = false)
    private Institute institute;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'student'")
    @Column(name = "member_role", columnDefinition = "institute_member_role not null")
    private InstituteMemberRole memberRole;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'active'")
    @Column(name = "status", columnDefinition = "user_status not null")
    private UserStatus status;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "joined_at", nullable = false)
    private OffsetDateTime joinedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Institute getInstitute() {
        return institute;
    }

    public void setInstitute(Institute institute) {
        this.institute = institute;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public InstituteMemberRole getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(InstituteMemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public OffsetDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(OffsetDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}