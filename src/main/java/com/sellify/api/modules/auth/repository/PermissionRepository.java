package com.sellify.api.modules.auth.repository;

import com.sellify.api.modules.auth.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository
    extends JpaRepository<Permission, String> {
}
