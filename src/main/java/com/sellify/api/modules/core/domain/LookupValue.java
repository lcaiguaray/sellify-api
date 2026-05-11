package com.sellify.api.modules.core.domain;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.sellify.api.common.auditing.GlobalEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lookup_values", schema = "core", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "lookup_group_id", "code" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LookupValue extends GlobalEntity {

    @Column(name = "lookup_group_id", nullable = false)
    private String lookupGroupId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private Map<String, Object> attributes = new HashMap<>();

    @PrePersist
    @PreUpdate
    void ensureUpperCase() {
        if (code != null)
            code = code.trim().toUpperCase();
    }
}