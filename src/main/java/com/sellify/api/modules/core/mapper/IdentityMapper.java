package com.sellify.api.modules.core.mapper;

import org.springframework.stereotype.Component;

import com.sellify.api.modules.people.domain.Identity;
import com.sellify.api.modules.people.dto.IdentityResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IdentityMapper {

    private final LookupValueMapper lookupValueMapper;

    public IdentityResponse toResponse(Identity entity) {
        if (entity == null) {
            return null;
        }

        return new IdentityResponse(
            lookupValueMapper.toResponse(entity.getDocumentType()),
            lookupValueMapper.toResponse(entity.getGender()),
            lookupValueMapper.toResponse(entity.getCivilStatus()),
            lookupValueMapper.toResponse(entity.getEducationLevel()),
            entity.getId(),
            entity.getIsLegalEntity(),
            entity.getTaxId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getBusinessName(),
            entity.getTradeName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getInceptionDate(),
            entity.getActive()
        );
    }

}
