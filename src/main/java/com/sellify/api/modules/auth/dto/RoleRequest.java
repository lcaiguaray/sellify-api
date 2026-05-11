package com.sellify.api.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleRequest(
    @NotBlank(message = "{validation.notblank}")
    String name,
    String description
) {}
