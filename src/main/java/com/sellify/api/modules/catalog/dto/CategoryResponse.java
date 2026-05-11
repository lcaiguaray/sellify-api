package com.sellify.api.modules.catalog.dto;

import java.util.UUID;

public record CategoryResponse(
    UUID id,
    UUID parentId,
    String name,
    String slug,
    String description,
    boolean active
) {}