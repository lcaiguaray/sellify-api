package com.sellify.api.modules.auth.mapper;

import org.springframework.stereotype.Component;

import com.sellify.api.modules.auth.domain.UserCompany;
import com.sellify.api.modules.auth.dto.UserCompanyResponse;
import com.sellify.api.modules.core.mapper.CompanyMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCompanyMapper {

    private final CompanyMapper companyMapper;

    public UserCompanyResponse toResponse(UserCompany entity) {
        if (entity == null) {
            return null;
        }

        return new UserCompanyResponse(
            companyMapper.toResponse(entity.getCompany()),
            entity.getUser().getId(),
            entity.getIsDefault()
        );
    }

}
