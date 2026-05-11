package com.sellify.api.modules.people.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.sellify.api.modules.core.dto.LookupValueResponse;

public record IdentityResponse(
    LookupValueResponse documentType,
    LookupValueResponse gender,
    LookupValueResponse civilStatus,
    LookupValueResponse educationLevel,
    UUID id,
    boolean isLegalEntity,
    String taxId,
    String firstName,
    String lastName,
    String businessName,
    String tradeName,
    String email,
    String phone,
    LocalDate inceptionDate,
    boolean active
) {}