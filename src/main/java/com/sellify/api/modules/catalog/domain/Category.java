package com.sellify.api.modules.catalog.domain;

import java.util.UUID;

import com.sellify.api.common.auditing.GlobalEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories", schema = "catalog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends GlobalEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    private String description;

}
