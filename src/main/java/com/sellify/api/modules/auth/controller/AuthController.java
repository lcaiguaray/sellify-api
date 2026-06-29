package com.sellify.api.modules.auth.controller;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sellify.api.common.config.MessageTranslator;
import com.sellify.api.common.exception.UnauthorizedException;
import com.sellify.api.common.response.ApiResponse;
import com.sellify.api.modules.auth.dto.*;
import com.sellify.api.modules.auth.service.AuthService;
import com.sellify.api.security.config.JwtProperties;
import com.sellify.api.security.domain.UserPrincipal;
import com.sellify.api.security.jwt.JwtService;

import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;
    private final MessageTranslator messageTranslator;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        AuthTokenResponse authToken = authService.login(request, httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"));

        jwtService.setCookieToken(httpResponse, authToken.accessToken().token(),
                authToken.accessToken().expiresAt().getTime() - System.currentTimeMillis(), "/");
        jwtService.setCookieRefreshToken(httpResponse, authToken.refreshToken().token(),
                authToken.refreshToken().expiresAt().getTime() - System.currentTimeMillis(), "/api/auth/refresh");

        String message = messageTranslator.getMessage("success.auth.login");
        return ResponseEntity.ok(ApiResponse.success(authToken.authResponse(), message));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (jwtProperties.refreshTokenName().equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("error.auth.token.missing");
        }

        JwtToken newAccessToken = authService.refreshAccessToken(refreshToken);
        jwtService.setCookieToken(response, newAccessToken.token(),
                newAccessToken.expiresAt().getTime() - System.currentTimeMillis(), "/");

        String message = messageTranslator.getMessage("success.auth.refresh");
        return ResponseEntity.ok(ApiResponse.success(null, message));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (jwtProperties.refreshTokenName().equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        authService.logout(refreshToken);

        jwtService.setCookieToken(response, "", 0L, "/");
        jwtService.setCookieRefreshToken(response, "", 0L, "/api/auth/refresh");

        String message = messageTranslator.getMessage("success.auth.logout");
        return ResponseEntity.ok(ApiResponse.success(null, message));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {
        AuthResponse userAuth = authService.me(principal.userId(), principal.companyId(), principal.roleId());
        String message = messageTranslator.getMessage("success.found");
        return ResponseEntity.ok(ApiResponse.success(userAuth, message));
    }
}
