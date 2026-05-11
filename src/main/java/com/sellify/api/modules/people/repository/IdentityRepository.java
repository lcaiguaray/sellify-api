package com.sellify.api.modules.people.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellify.api.modules.people.domain.Identity;

public interface IdentityRepository
    extends JpaRepository<Identity, UUID> {

    Optional<Identity> findByTaxId(String taxtId);

    boolean existsByTaxId(String taxtId);

}
