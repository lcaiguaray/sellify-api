package com.sellify.api.modules.auth.domain;


import com.sellify.api.common.auditing.CreatedAuditableEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_roles", schema = "auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends CreatedAuditableEntity {

    @EmbeddedId
    private UserRoleId id;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

}
