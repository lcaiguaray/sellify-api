package com.sellify.api.modules.auth.dto;

import java.util.UUID;

import com.sellify.api.modules.core.dto.CompanyResponse;

public record UserCompanyResponse(
    CompanyResponse company,
    UUID userId,
    boolean isDefault
) {}