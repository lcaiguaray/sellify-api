package com.sellify.api.modules.auth.service;

import com.sellify.api.common.exception.NotFoundException;
import com.sellify.api.common.response.PageResponse;
import com.sellify.api.modules.auth.domain.*;
import com.sellify.api.modules.auth.dto.*;
import com.sellify.api.modules.auth.mapper.RoleMapper;
import com.sellify.api.modules.auth.repository.*;
import com.sellify.api.modules.auth.repository.specification.RoleSpecification;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> searchByCompany(UUID companyId, String search, Boolean active, Pageable pageable) {
        
        Specification<Role> spec = Specification
            .where(RoleSpecification.byCompanyId(companyId))
            .and(RoleSpecification.isActive(active))
            .and(RoleSpecification.search(search));

        Page<Role> rolePage = roleRepository.findAll(spec, pageable);

        List<RoleResponse> content = rolePage.getContent().stream()
                .map(roleMapper::toResponse)
                .toList();

        return PageResponse.<RoleResponse>builder()
            .content(content)
            .pageNumber(rolePage.getNumber())
            .pageSize(rolePage.getSize())
            .totalElements(rolePage.getTotalElements())
            .totalPages(rolePage.getTotalPages())
            .isLast(rolePage.isLast())
            .build();
    }

    @Transactional(readOnly = true)
    public Role requireById(UUID id) {
        return roleRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new NotFoundException("error.role.notfound"));
    }

    @Transactional(readOnly = true)
    public Optional<Role> findDefaultByUserIdAndCompanyId(UUID id, UUID companyId) {
        return roleRepository.findDefaultByUserIdAndCompanyId(id, companyId);
    }

    @Transactional(readOnly = true)
    public Set<String> getPermissionsByRoleId(UUID id) {
        return roleRepository.findPermissionIdsByRoleId(id);
    }

    @Transactional
    public Role create(RoleRequest request, UUID companyId) {
        Role role = new Role();
        role.setCompanyId(companyId);
        role.setName(request.name());
        role.setDescription(request.description());
        role.setActive(true);
        return roleRepository.save(role);
    }

    @Transactional
    public Role update(UUID id, RoleRequest request) {
        Role role = requireById(id);
        role.setName(request.name());
        role.setDescription(request.description());
        return role;
    }

    @Transactional
    public void setActive(UUID id, boolean active) {
        Role role = requireById(id);
        role.setActive(active);
    }

    @Transactional
    public void replacePermissions(UUID id, Set<String> permissionIds) {
        Role role = requireById(id);
        Set<Permission> permissions = new HashSet<>();
        permissionRepository.findAllById(permissionIds)
            .forEach(permissions::add);

        if (permissions.size() != permissionIds.size()) {
            throw new NotFoundException("error.role.permissionsnotfound");
        }

        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);
    }
}
