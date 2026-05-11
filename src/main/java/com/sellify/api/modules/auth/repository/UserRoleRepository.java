package com.sellify.api.modules.auth.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellify.api.modules.auth.domain.UserRole;
import com.sellify.api.modules.auth.domain.UserRoleId;

public interface UserRoleRepository
    extends JpaRepository<UserRole, UserRoleId> {
    
    boolean existsById_UserIdAndId_RoleId(UUID userId, UUID roleId);

    void deleteById_UserIdAndId_RoleId(UUID userId, UUID roleId);

    boolean existsById_UserIdAndIsDefaultTrue(UUID userId);
    
}
