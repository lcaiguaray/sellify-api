package com.sellify.api.modules.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;

import com.sellify.api.common.config.MessageTranslator;
import com.sellify.api.common.response.ApiResponse;
import com.sellify.api.common.response.PageResponse;
import com.sellify.api.modules.auth.domain.Role;
import com.sellify.api.modules.auth.dto.ReplacePermissionsRequest;
import com.sellify.api.modules.auth.dto.RoleRequest;
import com.sellify.api.modules.auth.dto.RoleResponse;
import com.sellify.api.modules.auth.dto.RoleSearchCriteria;
import com.sellify.api.modules.auth.mapper.RoleMapper;
import com.sellify.api.modules.auth.service.RoleService;
import com.sellify.api.security.domain.UserPrincipal;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;
    private final MessageTranslator messageTranslator;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE.READ')")
    public ResponseEntity<?> list(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid RoleSearchCriteria criteria
    ) {
        Sort sort = criteria.sortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) 
            ? Sort.by(criteria.sortBy()).ascending() 
            : Sort.by(criteria.sortBy()).descending();

        Pageable pageable = PageRequest.of(criteria.page(), criteria.size(), sort);
        PageResponse<RoleResponse> paginated = roleService.searchByCompany(principal.companyId(), criteria.search(), criteria.active(), pageable);
        return ResponseEntity.ok(ApiResponse.success(paginated, messageTranslator.getMessage("success.list")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE.READ')")
    public ResponseEntity<ApiResponse<RoleResponse>> findById(@PathVariable UUID id) {
        Role role = roleService.requireById(id);
        return ResponseEntity.ok(
            ApiResponse.success(
                roleMapper.toResponse(role),
                messageTranslator.getMessage("success.found")
            )
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE.WRITE')")
    public ResponseEntity<ApiResponse<RoleResponse>> create(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody RoleRequest request) {
        Role role = roleService.create(request, principal.companyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.created(
                roleMapper.toResponse(role),
                messageTranslator.getMessage("success.created")
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE.WRITE')")
    public ResponseEntity<ApiResponse<RoleResponse>> update(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID id, 
        @Valid @RequestBody RoleRequest request
    ) {
        Role role = roleService.update(id, request);
        return ResponseEntity.ok(
            ApiResponse.success(
                roleMapper.toResponse(role),
                messageTranslator.getMessage("success.updated")
            )
        );
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('ROLE.MANAGE')")
    public ResponseEntity<?> enable(@PathVariable UUID id) {
        roleService.setActive(id, true);
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.enabled")));
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('ROLE.MANAGE')")
    public ResponseEntity<?> disable(@PathVariable UUID id) {
        roleService.setActive(id, false);
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.disabled")));
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE.MANAGE')")
    public ResponseEntity<?> replacePermissions(
        @PathVariable UUID id,
        @Valid @RequestBody ReplacePermissionsRequest request
    ) {
        roleService.replacePermissions(id, request.permissionIds());
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.updated")));
    }
}
