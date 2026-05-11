package com.sellify.api.modules.core.mapper;

import org.springframework.stereotype.Component;

import com.sellify.api.modules.core.domain.Company;
import com.sellify.api.modules.core.dto.CompanyResponse;

@Component
public class CompanyMapper {

    public CompanyResponse toResponse(Company company) {
        if (company == null) {
            return null;
        }

        return new CompanyResponse(
            company.getId(),
            company.getTaxId(),
            company.getBusinessName(),
            company.getTradeName(),
            company.getLogoUrl(),
            company.getWebsiteUrl(),
            company.getActive()
        );
    }
}
