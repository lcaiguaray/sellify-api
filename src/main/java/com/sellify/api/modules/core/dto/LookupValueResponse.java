package com.sellify.api.modules.core.dto;

import java.util.UUID;

public record LookupValueResponse(
    UUID id,
    String lookupGroupId,
    String code,
    String name,
    String description,
    boolean active
) {}