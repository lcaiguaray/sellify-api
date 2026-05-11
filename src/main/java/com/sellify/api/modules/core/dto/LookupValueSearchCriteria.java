package com.sellify.api.modules.core.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record LookupValueSearchCriteria(
    @Min(value = 0, message = "{validation.min.0}")
    Integer page,
    @Min(value = 0, message = "{validation.min.0}")
    @Max(value = 100, message = "{validation.max}")
    Integer size,
    @NotBlank(message = "{validation.notblank}")
    String lookupGroupId,
    String search,
    Boolean active,
    String sortBy,
    String sortDir
) {
    public LookupValueSearchCriteria {
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sortBy == null) sortBy = "name";
        if (sortDir == null) sortDir = "asc";
    }
}