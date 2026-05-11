package com.sellify.api.modules.auth.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellify.api.common.exception.BusinessException;
import com.sellify.api.modules.auth.domain.User;
import com.sellify.api.modules.auth.domain.UserCompany;
import com.sellify.api.modules.auth.domain.UserCompanyId;
import com.sellify.api.modules.auth.repository.UserCompanyRepository;
import com.sellify.api.modules.core.domain.Company;
import com.sellify.api.modules.core.service.CompanyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCompanyService {

    private final UserCompanyRepository userCompanyRepository;
    private final CompanyService companyService;
    private final UserService userService;

    
    public List<UserCompany> getUserCompanies(UUID userId) {
        return userCompanyRepository.findAllById_UserId(userId);
    }


    @Transactional
    public void create(UUID userId, UUID companyId) {
        if (userCompanyRepository.existsByUser_IdAndCompany_Id(userId, companyId)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "error.user.already.company");
        }

        User user = userService.requireById(userId);
        Company company = companyService.requireById(companyId);
        boolean hasDefaultCompany = userCompanyRepository.existsByUser_IdAndIsDefaultTrue(userId);
        userCompanyRepository.save(
            new UserCompany(
                new UserCompanyId(userId, companyId),
                user,
                company,
                !hasDefaultCompany
            )
        );
    }

    @Transactional
    public void create(User user, UUID companyId) {
        if (userCompanyRepository.existsByUser_IdAndCompany_Id(user.getId(), companyId)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "error.user.already.company");
        }

        Company company = companyService.requireById(companyId);
        boolean hasDefaultCompany = userCompanyRepository.existsByUser_IdAndIsDefaultTrue(user.getId());
        userCompanyRepository.save(
            new UserCompany(
                new UserCompanyId(user.getId(), companyId),
                user,
                company,
                !hasDefaultCompany
            )
        );
    }
}
