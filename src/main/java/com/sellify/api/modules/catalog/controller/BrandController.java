package com.sellify.api.modules.catalog.controller;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.sellify.api.modules.catalog.domain.Brand;
import com.sellify.api.modules.catalog.dto.BrandRequest;
import com.sellify.api.modules.catalog.dto.BrandResponse;
import com.sellify.api.modules.catalog.dto.BrandSearchCriteria;
import com.sellify.api.modules.catalog.mapper.BrandMapper;
import com.sellify.api.modules.catalog.service.BrandService;
import com.sellify.api.security.domain.UserPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalog/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final BrandMapper brandMapper;
    private final MessageTranslator messageTranslator;

    @GetMapping
    @PreAuthorize("hasAuthority('BRAND.READ')")
    public ResponseEntity<?> list(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid BrandSearchCriteria criteria
    ) {
        Sort sort = criteria.sortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) 
            ? Sort.by(criteria.sortBy()).ascending() 
            : Sort.by(criteria.sortBy()).descending();

        Pageable pageable = PageRequest.of(criteria.page(), criteria.size(), sort);
        PageResponse<BrandResponse> paginated = brandService.searchByCompany(principal.companyId(), criteria.search(), criteria.active(), pageable);
        return ResponseEntity.ok(ApiResponse.success(paginated, messageTranslator.getMessage("success.list")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BRAND.READ')")
    public ResponseEntity<ApiResponse<BrandResponse>> findById(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID id
    ) {
        Brand brand = brandService.requireActiveByIdAndCompany(id, principal.companyId());
        return ResponseEntity.ok(
            ApiResponse.success(
                brandMapper.toResponse(brand),
                messageTranslator.getMessage("success.found")
            )
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BRAND.WRITE')")
    public ResponseEntity<ApiResponse<BrandResponse>> create(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody BrandRequest request
    ) {
        Brand brand = brandService.create(principal.companyId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.created(
                brandMapper.toResponse(brand),
                messageTranslator.getMessage("success.created")
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BRAND.WRITE')")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID id, 
        @Valid @RequestBody BrandRequest request
    ) {
        Brand brand = brandService.update(id, principal.companyId(), request);
        return ResponseEntity.ok(
            ApiResponse.success(
                brandMapper.toResponse(brand),
                messageTranslator.getMessage("success.updated")
            )
        );
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('BRAND.MANAGE')")
    public ResponseEntity<ApiResponse<Void>> enable(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID id
    ) {
        brandService.setActive(id, principal.companyId(), true);
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.enabled")));
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('BRAND.MANAGE')")
    public ResponseEntity<ApiResponse<Void>> disable(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID id
    ) {
        brandService.setActive(id, principal.companyId(), false);
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.disabled")));
    }

}
