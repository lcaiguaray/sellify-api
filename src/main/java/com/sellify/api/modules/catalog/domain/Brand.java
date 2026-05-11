package com.sellify.api.modules.catalog.domain;

import java.util.UUID;

import com.sellify.api.common.auditing.GlobalEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands", schema = "catalog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends GlobalEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

}
