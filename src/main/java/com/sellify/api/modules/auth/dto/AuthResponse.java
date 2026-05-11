package com.sellify.api.modules.auth.dto;

import java.util.List;

import com.sellify.api.modules.core.dto.CompanyResponse;

public record AuthResponse(
    UserResponse user,
    RoleResponse role,
    CompanyResponse company,
    List<UserCompanyResponse> userCompanies,
    String[] permissions
) {}
