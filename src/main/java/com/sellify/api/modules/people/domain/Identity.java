package com.sellify.api.modules.people.domain;

import java.time.LocalDate;

import com.sellify.api.common.auditing.GlobalEntity;
import com.sellify.api.modules.core.domain.LookupValue;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "identities", schema = "people")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Identity extends GlobalEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private LookupValue documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id")
    private LookupValue gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "civil_status_id")
    private LookupValue civilStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_level_id")
    private LookupValue educationLevel;

    @Column(name = "is_legal_entity", nullable = false)
    private Boolean isLegalEntity = false;

    @Column(name = "tax_id", nullable = false, unique = true)
    private String taxId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "trade_name")
    private String tradeName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", columnDefinition = "TEXT")
    private String phone;

    @Column(name = "inception_date")
    private LocalDate inceptionDate;

}
