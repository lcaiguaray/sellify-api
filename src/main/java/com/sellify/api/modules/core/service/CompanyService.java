package com.sellify.api.modules.core.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellify.api.common.exception.BusinessException;
import com.sellify.api.common.exception.NotFoundException;
import com.sellify.api.modules.core.domain.Company;
import com.sellify.api.modules.core.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public Company requireById(UUID id) {
        return companyRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("error.company.notfound"));
    }

    @Transactional(readOnly = true)
    public Company findDefaultByUserId(UUID id) {
        return companyRepository.findDefaultByUserId(id)
            .orElseThrow(() -> new BusinessException("error.auth.company.nodefault"));
    }
}
