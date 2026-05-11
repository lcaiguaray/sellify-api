package com.sellify.api.modules.core.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sellify.api.modules.core.domain.LookupValue;

public interface LookupValueRepository
    extends JpaRepository<LookupValue, UUID>, JpaSpecificationExecutor<LookupValue> {

    Optional<LookupValue> findByIdAndLookupGroupIdAndActiveTrue(UUID id, String lookupGroupId);

    Optional<LookupValue> findByIdAndActiveTrue(UUID id);

    boolean existsByCodeAndLookupGroupId(String code, String lookupGroupId);

}
