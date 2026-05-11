package com.sellify.api.modules.catalog.dto;

import java.util.UUID;

import com.sellify.api.common.validation.ValidSlug;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    UUID parentId,
    @NotBlank(message = "{validation.notblank}")
    String name,
    @ValidSlug()
    @NotBlank(message = "{validation.notblank}")
    String slug,
    String description
) {}