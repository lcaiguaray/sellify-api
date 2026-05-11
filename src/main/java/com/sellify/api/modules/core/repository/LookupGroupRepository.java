package com.sellify.api.modules.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sellify.api.modules.core.domain.LookupGroup;

public interface LookupGroupRepository
    extends JpaRepository<LookupGroup, String>, JpaSpecificationExecutor<LookupGroup> {

    Optional<LookupGroup> findByIdAndActiveTrue(String id);
}
