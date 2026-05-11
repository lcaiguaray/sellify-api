package com.sellify.api.modules.auth.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sellify.api.common.exception.BusinessException;
import com.sellify.api.modules.auth.domain.UserRole;
import com.sellify.api.modules.auth.domain.UserRoleId;
import com.sellify.api.modules.auth.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Transactional
    public void create(UUID userId, UUID roleId) {
        if (userRoleRepository.existsById_UserIdAndId_RoleId(userId, roleId)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "error.user.already.company");
        }

        boolean hasDefaultRole = userRoleRepository.existsById_UserIdAndIsDefaultTrue(userId);
        userRoleRepository.save(
            new UserRole(
                new UserRoleId(userId, roleId),
                !hasDefaultRole
            )
        );
    }

    @Transactional
    public void delete(UUID userId, UUID roleId) {
        userRoleRepository.deleteById_UserIdAndId_RoleId(userId, roleId);
    }

}
