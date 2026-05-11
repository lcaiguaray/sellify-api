package com.sellify.api.modules.core.dto;

import java.util.UUID;

public record CompanyResponse(
    UUID id,
    String taxId,
    String businessName,
    String tradeName,
    String logoUrl,
    String websiteUrl,
    boolean active
) {}