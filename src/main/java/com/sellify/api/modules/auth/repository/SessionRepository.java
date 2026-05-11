package com.sellify.api.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellify.api.modules.auth.domain.Session;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    Optional<Session> findByTokenAndRevokedFalse(String token);

}
