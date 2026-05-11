package com.sellify.api.modules.auth.mapper;

import org.springframework.stereotype.Component;

import com.sellify.api.modules.auth.domain.User;
import com.sellify.api.modules.auth.dto.UserCreateRequest;
import com.sellify.api.modules.auth.dto.UserResponse;
import com.sellify.api.modules.core.dto.IdentityRequest;
import com.sellify.api.modules.core.mapper.IdentityMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final IdentityMapper identityMapper;

    public UserResponse toResponse(User entity) {
        if (entity == null) {
            return null;
        }

        return new UserResponse(
            identityMapper.toResponse(entity.getIdentity()),
            entity.getId(),
            entity.getUsername(),
            entity.getActive()
        );
    }

    public IdentityRequest toIdentityRequest(UserCreateRequest request) {
        if (request == null) {
            return null;
        }

        return new IdentityRequest(
            request.documentTypeId(),
            null,
            null,
            null,
            request.taxId(),
            request.firstName(),
            request.lastName(),
            request.businessName(),
            request.tradeName(),
            request.email(),
            request.phone(),
            request.inceptionDate()
        );
    }
}
