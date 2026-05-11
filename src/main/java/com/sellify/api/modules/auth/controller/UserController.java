package com.sellify.api.modules.auth.controller;

import com.sellify.api.common.config.MessageTranslator;
import com.sellify.api.common.response.ApiResponse;
import com.sellify.api.common.response.PageResponse;
import com.sellify.api.modules.auth.domain.User;
import com.sellify.api.modules.auth.dto.UserCreateRequest;
import com.sellify.api.modules.auth.dto.UserResponse;
import com.sellify.api.modules.auth.dto.UserSearchCriteria;
import com.sellify.api.modules.auth.mapper.UserMapper;
import com.sellify.api.modules.auth.service.UserCompanyService;
import com.sellify.api.modules.auth.service.UserService;
import com.sellify.api.security.domain.UserPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserCompanyService userCompanyService;
    private final UserMapper userMapper;
    private final MessageTranslator messageTranslator;

    @GetMapping
    @PreAuthorize("hasAuthority('USER.READ')")
    public ResponseEntity<?> list(
        @AuthenticationPrincipal UserPrincipal principal,
        UserSearchCriteria criteria
    ) {
        Sort sort = criteria.sortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) 
            ? Sort.by(criteria.sortBy()).ascending() 
            : Sort.by(criteria.sortBy()).descending();

        Pageable pageable = PageRequest.of(criteria.page(), criteria.size(), sort);
        PageResponse<UserResponse> paginated = userService.pageableByCompany(principal.companyId(), criteria.search(), criteria.active(), pageable);
        return ResponseEntity.ok(ApiResponse.success(paginated, messageTranslator.getMessage("success.list")));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER.WRITE')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody UserCreateRequest request
    ) {
        User user = userService.create(request, principal.companyId());
        userCompanyService.create(user, principal.companyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.created(
                userMapper.toResponse(user),
                messageTranslator.getMessage("success.created")
            )
        );
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('USER.MANAGE')")
    public ResponseEntity<?> enable(@PathVariable UUID id) {
        userService.setActive(id, true);
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.enabled")));
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('USER.MANAGE')")
    public ResponseEntity<?> disable(@PathVariable UUID id) {
        userService.setActive(id, false);
        return ResponseEntity.ok(ApiResponse.success(null, messageTranslator.getMessage("success.disabled")));
    }
}
