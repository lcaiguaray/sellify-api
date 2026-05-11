package com.sellify.api.modules.core.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record IdentityRequest(
    @NotNull(message = "{validation.notblank}")
    UUID documentTypeId,
    UUID genderId,
    UUID civilStatusId,
    UUID educationLevelId,
    @NotNull(message = "{validation.notblank}")
    String taxId,
    String firstName,
    String lastName,
    String businessName,
    String tradeName,
    String email,
    String phone,
    LocalDate inceptionDate
) {}
