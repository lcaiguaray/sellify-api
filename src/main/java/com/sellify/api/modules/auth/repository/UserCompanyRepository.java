package com.sellify.api.modules.auth.repository;

import com.sellify.api.modules.auth.domain.UserCompany;
import com.sellify.api.modules.auth.domain.UserCompanyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCompanyRepository
    extends JpaRepository<UserCompany, UserCompanyId> {
    List<UserCompany> findAllById_UserId(UUID userId);
    Optional<UserCompany> findByUser_IdAndIsDefaultTrue(UUID userId);
    boolean existsByUser_IdAndIsDefaultTrue(UUID userId);
    boolean existsByUser_IdAndCompany_Id(UUID userId, UUID companyId);
}
