package com.sellify.api.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secret,
    boolean secureCookie,
    long expiration,
    String tokenName,
    long refreshExpiration,
    String refreshTokenName,
    Claims claims
) {
    public record Claims(
        String roleId,
        String companyId
    ) {}
}
