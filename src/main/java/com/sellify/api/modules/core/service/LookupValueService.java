package com.sellify.api.modules.core.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellify.api.common.enums.LookupGroupCode;
import com.sellify.api.common.exception.BusinessException;
import com.sellify.api.common.exception.FieldValidationException;
import com.sellify.api.common.exception.NotFoundException;
import com.sellify.api.common.response.PageResponse;
import com.sellify.api.modules.core.domain.LookupGroup;
import com.sellify.api.modules.core.domain.LookupValue;
import com.sellify.api.modules.core.dto.LookupValueRequest;
import com.sellify.api.modules.core.dto.LookupValueResponse;
import com.sellify.api.modules.core.mapper.LookupValueMapper;
import com.sellify.api.modules.core.repository.LookupValueRepository;
import com.sellify.api.modules.core.repository.specification.LookupValueSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LookupValueService {

    private final LookupGroupService lookupGroupService;

    private final LookupValueRepository lookupValueRepository;
    private final LookupValueMapper lookupValueMapper;

    @Transactional(readOnly = true)
    public PageResponse<LookupValueResponse> searchByLookupGroup(String lookupGroupId, String search, Boolean active, Pageable pageable) {
        
        Specification<LookupValue> spec = Specification
            .where(LookupValueSpecification.byLookupGroup(lookupGroupId))
            .and(LookupValueSpecification.isActive(active))
            .and(LookupValueSpecification.search(search));

        Page<LookupValue> page = lookupValueRepository.findAll(spec, pageable);

        List<LookupValueResponse> content = page.getContent().stream()
                .map(lookupValueMapper::toResponse)
                .toList();

        return PageResponse.<LookupValueResponse>builder()
            .content(content)
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .isLast(page.isLast())
            .build();
    }

    @Transactional(readOnly = true)
    public LookupValue requireActiveById(UUID id) {
        return lookupValueRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new NotFoundException("error.lookupvalue.notfound"));
    }

    @Transactional(readOnly = true)
    public LookupValue requireActiveByIdAndGroupId(UUID id, String lookupGroupId) {
        return lookupValueRepository.findByIdAndLookupGroupIdAndActiveTrue(id, lookupGroupId)
            .orElseThrow(() -> {
                if (lookupGroupId == LookupGroupCode.DocumentType.getCode())
                    throw new NotFoundException("error.lookupvalue.documenttype.notfound");

                if (lookupGroupId == LookupGroupCode.Gender.getCode())
                    throw new NotFoundException("error.lookupvalue.gender.notfound");

                if (lookupGroupId == LookupGroupCode.CivilStatus.getCode())
                    throw new NotFoundException("error.lookupvalue.civilstatus.notfound");

                if (lookupGroupId == LookupGroupCode.EducationLevel.getCode())
                    throw new NotFoundException("error.lookupvalue.educationlevel.notfound");

                throw new NotFoundException("error.lookupvalue.notfound");
            });
    }

    @Transactional
    public LookupValue create(LookupValueRequest request) {
        if (request.lookupGroupId() == null || request.lookupGroupId().isBlank()) {
            throw new FieldValidationException("lookupGroupId", "validation.notblank");
        }

        LookupGroup lookupGroup = lookupGroupService.requireActiveById(request.lookupGroupId());
        if (lookupValueRepository.existsByCodeAndLookupGroupId(request.code(), lookupGroup.getId())) {
            throw new BusinessException(HttpStatus.CONFLICT, "error.lookupvalue.duplicate");
        }
        
        return lookupValueRepository.save(
            new LookupValue(
                lookupGroup.getId(),
                request.code(),
                request.name(),
                request.description(),
                request.attributes()
            )
        );
    }

    @Transactional
    public LookupValue update(UUID id, LookupValueRequest request) {
        LookupValue lookupValue = requireActiveById(id);
        if (lookupValue.getCode() != request.code() && lookupValueRepository.existsByCodeAndLookupGroupId(request.code(), lookupValue.getLookupGroupId())) {
            throw new BusinessException(HttpStatus.CONFLICT, "error.lookupvalue.duplicate");
        }

        lookupValue.setCode(request.code());
        lookupValue.setName(request.name());
        lookupValue.setDescription(request.description());
        lookupValue.setAttributes(request.attributes());
        return lookupValue;
    }

    @Transactional
    public void setActive(UUID id, boolean active) {
        LookupValue lookupValue = requireActiveById(id);
        lookupValue.setActive(active);
    }
}
