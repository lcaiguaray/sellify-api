package com.sellify.api.modules.auth.service;

import com.sellify.api.common.exception.BusinessException;
import com.sellify.api.common.exception.NotFoundException;
import com.sellify.api.common.response.PageResponse;
import com.sellify.api.modules.auth.domain.Role;
import com.sellify.api.modules.auth.domain.User;
import com.sellify.api.modules.auth.dto.UserCreateRequest;
import com.sellify.api.modules.auth.dto.UserResponse;
import com.sellify.api.modules.auth.mapper.UserMapper;
import com.sellify.api.modules.auth.repository.UserRepository;
import com.sellify.api.modules.auth.repository.specification.UserSpecification;
import com.sellify.api.modules.core.service.IdentityService;
import com.sellify.api.modules.people.domain.Identity;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IdentityService identityService;
    private final UserRoleService userRoleService;
    private final RoleService roleService;

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> pageableByCompany(UUID companyId, String search, Boolean active, Pageable pageable) {
        
        Specification<User> spec = Specification
            .where(UserSpecification.byCompanyId(companyId))
            .and(UserSpecification.isActive(active))
            .and(UserSpecification.search(search));

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserResponse> content = userPage.getContent().stream()
            .map(userMapper::toResponse)
            .toList();

        return PageResponse.<UserResponse>builder()
            .content(content)
            .pageNumber(userPage.getNumber())
            .pageSize(userPage.getSize())
            .totalElements(userPage.getTotalElements())
            .totalPages(userPage.getTotalPages())
            .isLast(userPage.isLast())
            .build();
    }

    @Transactional(readOnly = true)
    public User requireById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("error.user.notfound"));
    }

    @Transactional(readOnly = true)
    public User requireByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("error.user.notfound"));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByIdentityId(UUID identityId) {
        return userRepository.findByIdentity_Id(identityId);
    }
    
    @Transactional
    public User create(UserCreateRequest request, UUID companyId) {
        Identity identity = (request.identityId() != null)
            ? identityService.update(request.identityId(), userMapper.toIdentityRequest(request))
            : identityService.create(userMapper.toIdentityRequest(request));

        User user = findByIdentityId(identity.getId())
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setIdentity(identity);
                newUser.setUsername(identity.getTaxId());
                newUser.setPasswordHash(passwordEncoder.encode(identity.getTaxId()));
                newUser.setActive(true);
                return userRepository.save(newUser);
            });
        return user;
    }

    @Transactional
    public User updateUsername(UUID id, String username) {
        User user = requireById(id);
        if (userRepository.existsByUsernameAndIdNot(username, user.getId())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "error.user.duplicate");
        }

        user.setUsername(username);
        return user;
    }

    @Transactional
    public void resetPassword(UUID id, String password) {
        User user = requireById(id);
        user.setPasswordHash(passwordEncoder.encode(password));
    }

    @Transactional
    public void setActive(UUID id, boolean active) {
        User user = requireById(id);
        user.setActive(active);
    }

    @Transactional
    public void assignRole(UUID userId, UUID roleId) {
        User user = requireById(userId);
        Role role = roleService.requireById(roleId);
        userRoleService.create(user.getId(), role.getId());
    }

    @Transactional
    public void removeRole(UUID userId, UUID roleId) {
        userRoleService.delete(userId, roleId);
    }

    @Transactional
    public void updateLogin(User user) {
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
    }
}
