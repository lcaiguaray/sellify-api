package com.sellify.api.modules.catalog.dto;

import java.util.UUID;

public record BrandResponse(
    UUID id,
    String name,
    String slug,
    String description,
    String logoUrl,
    boolean active
) {}