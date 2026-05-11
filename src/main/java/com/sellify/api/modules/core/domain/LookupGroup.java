package com.sellify.api.modules.core.domain;

import com.sellify.api.common.auditing.DateAuditableEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lookup_groups", schema = "core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LookupGroup extends DateAuditableEntity {
    
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
    
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    @PreUpdate
    private void ensureUpperCaseId() {
        if (this.id != null) {
            this.id = this.id.trim().toUpperCase();
        }
    }

}