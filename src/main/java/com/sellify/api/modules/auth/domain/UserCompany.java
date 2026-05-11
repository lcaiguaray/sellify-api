package com.sellify.api.modules.auth.domain;

import com.sellify.api.common.auditing.CreatedAuditableEntity;
import com.sellify.api.modules.core.domain.Company;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_companies", schema = "auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCompany extends CreatedAuditableEntity {

    @EmbeddedId
    private UserCompanyId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("companyId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

}
