package com.sellify.api.modules.auth.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellify.api.common.exception.BusinessException;
import com.sellify.api.modules.auth.domain.Session;
import com.sellify.api.modules.auth.dto.JwtToken;
import com.sellify.api.modules.auth.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Transactional(readOnly = true)
    public Session requireByTokenAndRevokedFalse(String token) {
        return sessionRepository.findByTokenAndRevokedFalse(token)
            .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "error.auth.token.invalid"));
    }

    @Transactional
    public Session create(UUID userId, UUID companyId, JwtToken token, String ipAddress, String userAgent) {
        Session session = new Session();
        session.setUserId(userId);
        session.setCompanyId(companyId);
        session.setToken(token.token());
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent != null ? userAgent : "Unknown");
        session.setExpiresAt(token.expiresAt().toInstant());
        session.setRevoked(false);
        return sessionRepository.save(session);
    }

    @Transactional
    public void revokeToken(String token) {
        if (token == null) return;
        
        sessionRepository.findByTokenAndRevokedFalse(token)
            .ifPresent(session -> {
                session.setRevoked(true);
                sessionRepository.save(session);
            });
    }
}
