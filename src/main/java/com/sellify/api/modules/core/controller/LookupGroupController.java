package com.sellify.api.modules.core.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellify.api.common.config.MessageTranslator;
import com.sellify.api.common.response.ApiResponse;
import com.sellify.api.common.response.PageResponse;
import com.sellify.api.modules.core.domain.LookupGroup;
import com.sellify.api.modules.core.dto.LookupGroupResponse;
import com.sellify.api.modules.core.dto.LookupGroupSearchCriteria;
import com.sellify.api.modules.core.mapper.LookupGroupMapper;
import com.sellify.api.modules.core.service.LookupGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/core/lookup-groups")
@RequiredArgsConstructor
public class LookupGroupController {

    private final LookupGroupService lookupGroupService;
    private final LookupGroupMapper lookupGroupMapper;
    private final MessageTranslator messageTranslator;

    @GetMapping
    @PreAuthorize("hasAuthority('LOOKUP_GROUP.READ')")
    public ResponseEntity<?> list(@Valid LookupGroupSearchCriteria criteria) {
        Sort sort = criteria.sortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) 
            ? Sort.by(criteria.sortBy()).ascending() 
            : Sort.by(criteria.sortBy()).descending();

        Pageable pageable = PageRequest.of(criteria.page(), criteria.size(), sort);
        PageResponse<LookupGroupResponse> paginatedRoles = lookupGroupService.search(criteria.search(), criteria.active(), pageable);
        return ResponseEntity.ok(ApiResponse.success(paginatedRoles, messageTranslator.getMessage("success.list")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOOKUP_GROUP.READ')")
    public ResponseEntity<ApiResponse<LookupGroupResponse>> findById(@PathVariable String id) {
        LookupGroup lookupGroup = lookupGroupService.requireActiveById(id);
        return ResponseEntity.ok(
            ApiResponse.success(
                lookupGroupMapper.toResponse(lookupGroup),
                messageTranslator.getMessage("success.found")
            )
        );
    }

}
