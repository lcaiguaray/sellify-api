package com.sellify.api.security.domain;

import java.util.UUID;

public record UserPrincipal(
    UUID userId,
    UUID companyId,
    UUID roleId
) {}
