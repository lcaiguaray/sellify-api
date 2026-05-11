package com.sellify.api.modules.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellify.api.common.exception.NotFoundException;
import com.sellify.api.common.response.PageResponse;
import com.sellify.api.modules.core.domain.LookupGroup;
import com.sellify.api.modules.core.dto.LookupGroupResponse;
import com.sellify.api.modules.core.mapper.LookupGroupMapper;
import com.sellify.api.modules.core.repository.LookupGroupRepository;
import com.sellify.api.modules.core.repository.specification.LookupGroupSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LookupGroupService {

    private final LookupGroupRepository lookupGroupRepository;
    private final LookupGroupMapper lookupGroupMapper;

    @Transactional(readOnly = true)
    public PageResponse<LookupGroupResponse> search(String search, Boolean active, Pageable pageable) {
        
        Specification<LookupGroup> spec = Specification
            .where(LookupGroupSpecification.isActive(active))
            .and(LookupGroupSpecification.search(search));

        Page<LookupGroup> page = lookupGroupRepository.findAll(spec, pageable);

        List<LookupGroupResponse> content = page.getContent().stream()
                .map(lookupGroupMapper::toResponse)
                .toList();

        return PageResponse.<LookupGroupResponse>builder()
            .content(content)
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .isLast(page.isLast())
            .build();
    }

    @Transactional(readOnly = true)
    public LookupGroup requireActiveById(String id) {
        return lookupGroupRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new NotFoundException("error.lookupgroup.notfound"));
    }

}
