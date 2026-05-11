package com.sellify.api.modules.auth.dto;

public record AuthTokenResponse(
    AuthResponse authResponse,
    JwtToken accessToken,
    JwtToken refreshToken
) {}
