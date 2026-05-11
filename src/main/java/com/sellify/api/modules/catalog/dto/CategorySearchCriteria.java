package com.sellify.api.modules.catalog.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CategorySearchCriteria(
    @Min(value = 0, message = "{validation.min.0}")
    Integer page,
    @Min(value = 0, message = "{validation.min.0}")
    @Max(value = 100, message = "{validation.max}")
    Integer size,
    UUID parentId,
    String search,
    Boolean active,
    String sortBy,
    String sortDir
) {
    public CategorySearchCriteria {
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sortBy == null) sortBy = "name";
        if (sortDir == null) sortDir = "asc";
    }
}