package com.sellify.api.modules.core.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sellify.api.common.enums.LookupGroupCode;
import com.sellify.api.common.exception.FieldValidationException;
import com.sellify.api.common.exception.NotFoundException;
import com.sellify.api.modules.core.domain.LookupValue;
import com.sellify.api.modules.core.dto.IdentityRequest;
import com.sellify.api.modules.core.util.DocumentTypeHelper;
import com.sellify.api.modules.people.domain.Identity;
import com.sellify.api.modules.people.repository.IdentityRepository;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final LookupValueService lookupValueService;
    
    private final IdentityRepository identityRepository;

    private final DocumentTypeHelper documentTypeHelper;

    private void validateIdentityFields(boolean isLegalEntity, IdentityRequest request) {
        Map<String, String> errors = new HashMap<>();
        
        if (isLegalEntity) {
            if (request.businessName() == null || request.businessName().isBlank()) {
                errors.put("businessName", "validation.notblank");
            }
            if (request.tradeName() == null || request.tradeName().isBlank()) {
                errors.put("tradeName", "validation.notblank");
            }
        } else {
            if (request.firstName() == null || request.firstName().isBlank()) {
                errors.put("firstName", "validation.notblank");
            }
            if (request.lastName() == null || request.lastName().isBlank()) {
                errors.put("lastName", "validation.notblank");
            }
        }

        if (!errors.isEmpty()) {
            throw new FieldValidationException(errors);
        }
    }

    @Transactional(readOnly = true)
    public Identity requireById(UUID id) {
        return identityRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("error.identity.notfound"));
    }
    
    @Transactional(readOnly = true)
    public boolean existsByTaxId(String taxId) {
        return identityRepository.existsByTaxId(taxId);
    }

    @Transactional
    public Identity create(IdentityRequest request) {
        if (identityRepository.existsByTaxId(request.taxId())) {
            throw new FieldValidationException("taxId", "error.identity.taxid.duplicate");
        }

        String documentTypeCode = LookupGroupCode.DocumentType.getCode();
        String genderCode = LookupGroupCode.Gender.getCode();
        String civilStatusCode = LookupGroupCode.CivilStatus.getCode();
        String educationLevelCode = LookupGroupCode.EducationLevel.getCode();

        LookupValue documentType = lookupValueService.requireActiveByIdAndGroupId(request.documentTypeId(), documentTypeCode);
        boolean isLegalEntity = documentTypeHelper.isLegalEntity(documentType, request.taxId());

        validateIdentityFields(isLegalEntity, request);

        LookupValue gender = null;
        LookupValue civilStatus = null;
        LookupValue educationLevel = null;

        if (!isLegalEntity) {
            if (request.genderId() != null) gender = lookupValueService.requireActiveByIdAndGroupId(request.genderId(), genderCode);
            if (request.civilStatusId() != null) civilStatus = lookupValueService.requireActiveByIdAndGroupId(request.civilStatusId(), civilStatusCode);
            if (request.educationLevelId() != null) educationLevel = lookupValueService.requireActiveByIdAndGroupId(request.educationLevelId(), educationLevelCode);
        }

        Identity identity = new Identity();
        identity.setDocumentType(documentType);
        identity.setGender(gender);
        identity.setCivilStatus(civilStatus);
        identity.setEducationLevel(educationLevel);
        identity.setTaxId(request.taxId());
        identity.setFirstName(!isLegalEntity ? request.firstName() : null);
        identity.setLastName(!isLegalEntity ? request.lastName() : null);
        identity.setBusinessName(isLegalEntity ? request.businessName() : null);
        identity.setTradeName(isLegalEntity ? request.tradeName() : null);
        identity.setEmail(request.email());
        identity.setPhone(request.phone());
        identity.setInceptionDate(request.inceptionDate());
        identity.setIsLegalEntity(isLegalEntity);
        return identity;
    }

    @Transactional
    public Identity update(UUID id, IdentityRequest request) {
        Identity identity = requireById(id);
        if (!identity.getTaxId().equals(request.taxId()) && identityRepository.existsByTaxId(request.taxId())) {
            throw new FieldValidationException("taxId", "error.identity.taxid.duplicate");
        }

        String documentTypeCode = LookupGroupCode.DocumentType.getCode();
        String genderCode = LookupGroupCode.Gender.getCode();
        String civilStatusCode = LookupGroupCode.CivilStatus.getCode();
        String educationLevelCode = LookupGroupCode.EducationLevel.getCode();

        LookupValue documentType = lookupValueService.requireActiveByIdAndGroupId(request.documentTypeId(), documentTypeCode);
        boolean isLegalEntity = documentTypeHelper.isLegalEntity(documentType, request.taxId());

        validateIdentityFields(isLegalEntity, request);

        LookupValue gender = null;
        LookupValue civilStatus = null;
        LookupValue educationLevel = null;

        if (!isLegalEntity) {
            if (request.genderId() != null) gender = lookupValueService.requireActiveByIdAndGroupId(request.genderId(), genderCode);
            if (request.civilStatusId() != null) civilStatus = lookupValueService.requireActiveByIdAndGroupId(request.civilStatusId(), civilStatusCode);
            if (request.educationLevelId() != null) educationLevel = lookupValueService.requireActiveByIdAndGroupId(request.educationLevelId(), educationLevelCode);
        }

        identity.setDocumentType(documentType);
        identity.setIsLegalEntity(isLegalEntity);
        identity.setTaxId(request.taxId());
        
        identity.setFirstName(!isLegalEntity ? request.firstName() : null);
        identity.setLastName(!isLegalEntity ? request.lastName() : null);
        identity.setBusinessName(isLegalEntity ? request.businessName() : null);
        identity.setTradeName(isLegalEntity ? request.tradeName() : null);
        
        identity.setGender(gender);
        identity.setCivilStatus(civilStatus);
        identity.setEducationLevel(educationLevel);
        
        identity.setEmail(request.email());
        identity.setPhone(request.phone());
        identity.setInceptionDate(request.inceptionDate());

        return identityRepository.save(identity);
    }
}
