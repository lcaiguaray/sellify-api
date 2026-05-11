package com.sellify.api.modules.catalog.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sellify.api.modules.catalog.domain.Category;

public interface CategoryRepository
    extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {

    Optional<Category> findByIdAndCompanyIdAndActiveTrue(UUID id, UUID companyId);

    Optional<Category> findByIdAndActiveTrue(UUID id);

    boolean existsBySlugAndCompanyId(String slug, UUID companyId);

}
