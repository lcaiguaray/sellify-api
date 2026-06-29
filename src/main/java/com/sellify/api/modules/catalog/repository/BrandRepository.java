package com.sellify.api.modules.catalog.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sellify.api.modules.catalog.domain.Brand;

public interface BrandRepository
    extends JpaRepository<Brand, UUID>, JpaSpecificationExecutor<Brand> {

    Optional<Brand> findByIdAndActiveTrue(UUID id);

    Optional<Brand> findByIdAndCompanyIdAndActiveTrue(UUID id, UUID companyId);

    Optional<Brand> findByIdAndCompanyId(UUID id, UUID companyId);

    boolean existsBySlugAndCompanyId(String slug, UUID companyId);
    
}
