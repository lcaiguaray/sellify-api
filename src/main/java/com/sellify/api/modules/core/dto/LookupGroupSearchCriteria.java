package com.sellify.api.modules.core.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record LookupGroupSearchCriteria(
    @Min(value = 0, message = "{validation.min.0}")
    Integer page,
    @Min(value = 0, message = "{validation.min.0}")
    @Max(value = 100, message = "{validation.max}")
    Integer size,
    String search,
    Boolean active,
    String sortBy,
    String sortDir
) {
    public LookupGroupSearchCriteria {
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sortBy == null) sortBy = "username";
        if (sortDir == null) sortDir = "asc";
    }
}