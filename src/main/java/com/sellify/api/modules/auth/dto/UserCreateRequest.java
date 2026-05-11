package com.sellify.api.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.UUID;

public record UserCreateRequest(
    UUID identityId,
    @NotNull(message = "{validation.notblank}")
    UUID documentTypeId,
    @NotNull(message = "{validation.notblank}")
    String taxId,
    String firstName,
    String lastName,
    String businessName,
    String tradeName,
    @NotNull(message = "{validation.notblank}")
    @Email(message = "{validation.email}")
    String email,
    String phone,
    @PastOrPresent(message = "{error.identity.inceptionDate.pastpresent}")
    LocalDate inceptionDate
    // @NotBlank(message = "{validation.notblank}")
    // @Size(min = 4, max = 50, message = "{validation.username.size}")
    // @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "{error.user.username.format}")
    // String username,
    // @NotBlank(message = "{validation.notblank}")
    // @Size(min = 6, max = 100, message = "{validation.size.min}")
    // String password
) {}
