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
import com.sellify.api.modules.catalog.domain.Category;
import com.sellify.api.modules.catalog.dto.CategoryRequest;
import com.sellify.api.modules.catalog.dto.CategoryResponse;
import com.sellify.api.modules.catalog.dto.CategorySearchCriteria;
import com.sellify.api.modules.catalog.mapper.CategoryMapper;
import com.sellify.api.modules.catalog.service.CategoryService;
import com.sellify.api.security.domain.UserPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalog/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final MessageTranslator messageTranslator;

    @GetMapping
    @PreAuthorize("hasAuthority('CATEGORY.READ')")
    public ResponseEntity<?> list(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid CategorySearchCriteria criteria
    ) {
        Sort sort = criteria.sortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) 
            ? Sort.by(criteria.sortBy()).ascending() 
            : Sort.by(criteria.sortBy()).descending();

        Pageable pageable = PageRequest.of(criteria.page(), criteria.size(), sort);
        PageResponse<CategoryResponse> paginated = categoryService.searchByCompany(
            principal.companyId(),
            criteria.parentId(),
            criteria.search(),
            criteria.active(),
            pageable
        );
        return ResponseEntity.ok(ApiResponse.success(paginated, messageTranslator.getMessage("success.list")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY.READ')")
    public ResponseEntity<ApiResponse<CategoryResponse>> findById(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID id
    ) {
        Category category = categoryService.requireActiveByIdAndCompany(id, principal.companyId());
        return ResponseEntity.ok(
            ApiResponse.success(
                categoryMapper.toResponse(category),
                messageTranslator.getMessage("success.found")
            )
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CATEGORY.WRITE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody CategoryRequest request
    ) {
        Category category = categoryService.create(request, principal.companyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.created(
                categoryMapper.toResponse(category),
                messageTranslator.getMessage("success.created")
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY.WRITE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID id, 
        @Valid @RequestBody CategoryRequest request
    ) {
        Category category = categoryService.update(id, principal.companyId(), request);
        return ResponseEntity.ok(
            ApiResponse.success(
                categoryMapper.toResponse(category),
                messageTranslator.getMessage("success.updated")
            )
        );
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('CATEGORY.MANAGE')")
    public ResponseEntity<ApiResponse<Void>> enable(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID id
    ) {
        categoryService.setActive(id, principal.companyId(), true);
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.enabled")));
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('CATEGORY.MANAGE')")
    public ResponseEntity<ApiResponse<Void>> disable(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID id
    ) {
        categoryService.setActive(id, principal.companyId(), false);
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.disabled")));
    }

}
