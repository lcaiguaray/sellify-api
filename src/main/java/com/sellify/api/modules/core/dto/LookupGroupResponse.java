package com.sellify.api.modules.core.dto;

public record LookupGroupResponse(
    String id,
    String name,
    String description,
    boolean active
) {}