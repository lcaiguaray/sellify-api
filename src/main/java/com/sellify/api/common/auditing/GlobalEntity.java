package com.sellify.api.common.auditing;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@MappedSuperclass
public abstract class GlobalEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
