package com.sellify.api.modules.auth.repository;

import com.sellify.api.modules.auth.domain.Role;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface RoleRepository
    extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {

    Optional<Role> findByIdAndCompanyId(UUID id, UUID companyId);

    Optional<Role> findByIdAndActiveTrue(UUID id);

    @Query("select p.id from Role r join r.permissions p where r.id = :roleId and r.active = true")
    Set<String> findPermissionIdsByRoleId(@Param("roleId") UUID roleId);


    @Query("""
        select r from Role r
        join UserRole ur on r.id = ur.id.roleId
        where ur.id.userId = :userId and r.companyId = :companyId and r.active = true and ur.isDefault = true
    """)
    Optional<Role> findDefaultByUserIdAndCompanyId(@Param("userId") UUID userId, @Param("companyId") UUID companyId);
}
