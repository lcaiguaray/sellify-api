package com.sellify.api.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellify.api.modules.auth.domain.User;

public interface UserRepository
    extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByIdentity_Id(UUID identityId);

    @Query("select u from User u join UserCompany uc on u.id = uc.id.userId where u.id = :userId and uc.id.companyId = :companyId")
    Optional<User> findByIdAndCompanyId(@Param("userId") UUID userId, @Param("companyId") UUID companyId);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, UUID id);

    boolean existsByIdentity_Id(UUID identityId);
}
