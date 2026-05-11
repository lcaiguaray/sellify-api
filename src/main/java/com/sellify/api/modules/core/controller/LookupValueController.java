package com.sellify.api.modules.core.controller;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellify.api.common.config.MessageTranslator;
import com.sellify.api.common.response.ApiResponse;
import com.sellify.api.common.response.PageResponse;
import com.sellify.api.modules.core.domain.LookupValue;
import com.sellify.api.modules.core.dto.LookupValueRequest;
import com.sellify.api.modules.core.dto.LookupValueResponse;
import com.sellify.api.modules.core.dto.LookupValueSearchCriteria;
import com.sellify.api.modules.core.mapper.LookupValueMapper;
import com.sellify.api.modules.core.service.LookupValueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/core/lookup-values")
@RequiredArgsConstructor
public class LookupValueController {

    private final LookupValueService lookupValueService;
    private final LookupValueMapper lookupValueMapper;
    private final MessageTranslator messageTranslator;

    @GetMapping
    @PreAuthorize("hasAuthority('LOOKUP_VALUE.READ')")
    public ResponseEntity<?> list(@Valid LookupValueSearchCriteria criteria) {
        Sort sort = criteria.sortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) 
            ? Sort.by(criteria.sortBy()).ascending() 
            : Sort.by(criteria.sortBy()).descending();

        Pageable pageable = PageRequest.of(criteria.page(), criteria.size(), sort);
        PageResponse<LookupValueResponse> paginatedRoles = lookupValueService.searchByLookupGroup(criteria.lookupGroupId(), criteria.search(), criteria.active(), pageable);
        return ResponseEntity.ok(ApiResponse.success(paginatedRoles, messageTranslator.getMessage("success.list")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOOKUP_VALUE.READ')")
    public ResponseEntity<ApiResponse<LookupValueResponse>> findById(@PathVariable UUID id) {
        LookupValue lookupValue = lookupValueService.requireActiveById(id);
        return ResponseEntity.ok(
            ApiResponse.success(
                lookupValueMapper.toResponse(lookupValue),
                messageTranslator.getMessage("success.found")
            )
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LOOKUP_VALUE.WRITE')")
    public ResponseEntity<ApiResponse<LookupValueResponse>> create(@Valid @RequestBody LookupValueRequest request) {
        LookupValue lookupValue = lookupValueService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.created(
                lookupValueMapper.toResponse(lookupValue),
                messageTranslator.getMessage("success.created")
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LOOKUP_VALUE.WRITE')")
    public ResponseEntity<ApiResponse<LookupValueResponse>> update(
        @PathVariable UUID id, 
        @Valid @RequestBody LookupValueRequest request
    ) {
        LookupValue lookupValue = lookupValueService.update(id, request);
        return ResponseEntity.ok(
            ApiResponse.success(
                lookupValueMapper.toResponse(lookupValue),
                messageTranslator.getMessage("success.updated")
            )
        );
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('LOOKUP_VALUE.MANAGE')")
    public ResponseEntity<?> enable(@PathVariable UUID id) {
        lookupValueService.setActive(id, true);
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.enabled")));
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('LOOKUP_VALUE.MANAGE')")
    public ResponseEntity<?> disable(@PathVariable UUID id) {
        lookupValueService.setActive(id, false);
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.disabled")));
    }
}
