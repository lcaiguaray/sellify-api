package com.sellify.api.modules.core.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellify.api.modules.core.domain.Company;

public interface CompanyRepository
    extends JpaRepository<Company, UUID> {

    @Query("select uc.company from UserCompany uc where uc.user.id = :userId and uc.isDefault = true")
    Optional<Company> findDefaultByUserId(@Param("userId") UUID userId);
}
