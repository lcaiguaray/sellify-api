package com.sellify.api.modules.core.domain;

import com.sellify.api.common.auditing.GlobalEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "companies", schema = "core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company extends GlobalEntity {
    
    @Column(name = "tax_id", nullable = false)
    private String taxId;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "trade_name", nullable = false)
    private String tradeName;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "website_url")
    private String websiteUrl;
    
}
