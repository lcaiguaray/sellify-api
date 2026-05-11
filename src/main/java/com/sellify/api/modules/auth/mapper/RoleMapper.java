package com.sellify.api.modules.auth.mapper;

import org.springframework.stereotype.Component;

import com.sellify.api.modules.auth.domain.Role;
import com.sellify.api.modules.auth.dto.RoleResponse;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }

        return new RoleResponse(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.getActive()
        );
    }
}
