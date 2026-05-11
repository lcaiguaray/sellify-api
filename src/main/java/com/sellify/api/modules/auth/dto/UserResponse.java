package com.sellify.api.modules.auth.dto;

import java.util.UUID;

import com.sellify.api.modules.people.dto.IdentityResponse;

public record UserResponse(
    IdentityResponse identity,
    UUID id,
    String username,
    boolean active
) {}
