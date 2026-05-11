package com.sellify.api.common.auditing;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreatedAuditableEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
