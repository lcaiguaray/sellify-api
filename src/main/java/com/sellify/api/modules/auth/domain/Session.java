package com.sellify.api.modules.auth.domain;

import java.time.Instant;
import java.util.UUID;

import com.sellify.api.common.auditing.DateAuditableEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sessions", schema = "auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session extends DateAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", nullable = false)
    private String userAgent;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt = Instant.now();

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

}
