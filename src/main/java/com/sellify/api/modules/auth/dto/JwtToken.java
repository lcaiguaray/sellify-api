package com.sellify.api.modules.auth.dto;

import java.util.Date;

public record JwtToken(
    String token,
    Date issuedAt,
    Date expiresAt
) {}