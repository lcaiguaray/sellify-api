package com.sellify.api.modules.catalog.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sellify.api.modules.catalog.domain.ProductVariant;

public interface ProductVariantRepository
    extends JpaRepository<ProductVariant, UUID>, JpaSpecificationExecutor<ProductVariant> {

}
