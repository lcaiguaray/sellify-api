package com.sellify.api.modules.catalog.dto;

import com.sellify.api.common.validation.ValidSlug;

import jakarta.validation.constraints.NotBlank;

public record BrandRequest(
    @NotBlank(message = "{validation.notblank}")
    String name,
    @ValidSlug()
    @NotBlank(message = "{validation.notblank}")
    String slug,
    String description
) {}