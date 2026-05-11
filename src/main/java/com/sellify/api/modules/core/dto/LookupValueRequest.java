package com.sellify.api.modules.core.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LookupValueRequest(
    String lookupGroupId,
    @NotBlank(message = "{validation.notblank}")
    @Size(min=2, max = 20, message = "{validation.size}")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "{validation.pattern.only-uppercase-numbers-hyphens}")
    String code,
    @NotBlank(message = "{validation.notblank}")
    String name,
    String description,
    Map<String, Object> attributes
) {}