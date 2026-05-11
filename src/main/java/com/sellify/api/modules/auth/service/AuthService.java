package com.sellify.api.modules.auth.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellify.api.common.exception.BusinessException;
import com.sellify.api.modules.auth.domain.Role;
import com.sellify.api.modules.auth.domain.Session;
import com.sellify.api.modules.auth.domain.User;
import com.sellify.api.modules.auth.domain.UserCompany;
import com.sellify.api.modules.auth.dto.AuthResponse;
import com.sellify.api.modules.auth.dto.AuthTokenResponse;
import com.sellify.api.modules.auth.dto.JwtToken;
import com.sellify.api.modules.auth.dto.LoginRequest;
import com.sellify.api.modules.auth.mapper.RoleMapper;
import com.sellify.api.modules.auth.mapper.UserCompanyMapper;
import com.sellify.api.modules.auth.mapper.UserMapper;
import com.sellify.api.modules.core.domain.Company;
import com.sellify.api.modules.core.mapper.CompanyMapper;
import com.sellify.api.modules.core.service.CompanyService;
import com.sellify.api.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final SessionService sessionService;
    private final UserCompanyService userCompanyService;

    private final CompanyMapper companyMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final UserCompanyMapper userCompanyMapper;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthTokenResponse login(LoginRequest request, String ipAddress, String userAgent) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userService.requireByUsername(request.username());
        List<UserCompany> userCompanies = userCompanyService.getUserCompanies(user.getId());
        Company company = userCompanies.stream()
            .filter(uc -> uc.getIsDefault())
            .findFirst()
            .map(UserCompany::getCompany)
            .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "error.auth.company.nodefault"));
        Role role = roleService.findDefaultByUserIdAndCompanyId(user.getId(), company.getId())
            .orElse(null);

        Set<String> permissions = Optional.ofNullable(role)
            .map(r -> roleService.getPermissionsByRoleId(r.getId()))
            .orElse(Collections.emptySet());

        UUID roleId = (role != null) ? role.getId() : null;
        JwtToken accessToken = jwtService.generateAccessToken(user, company.getId(), roleId);
        JwtToken refreshToken = jwtService.generateRefreshToken(user, company.getId(), roleId);

        sessionService.create(user.getId(), company.getId(), refreshToken, ipAddress, userAgent);
        userService.updateLogin(user);

        return new AuthTokenResponse(
            new AuthResponse(
                userMapper.toResponse(user),
                roleMapper.toResponse(role),
                companyMapper.toResponse(company),
                userCompanies.stream()
                    .map(userCompanyMapper::toResponse)
                    .toList(),
                permissions.toArray(new String[0])
            ),
            accessToken,
            refreshToken
        );
    }

    public AuthResponse me(UUID userId, UUID companyId, UUID roleId) {
        User user = userService.requireById(userId);
        Company company = companyService.requireById(companyId);
        List<UserCompany> userCompanies = userCompanyService.getUserCompanies(user.getId());
        Role role = Optional.ofNullable(roleId)
            .map(roleService::requireById)
            .orElse(null);

        Set<String> permissions = Optional.ofNullable(role)
            .map(r -> roleService.getPermissionsByRoleId(r.getId()))
            .orElse(Collections.emptySet());

        userService.updateLogin(user);
        return new AuthResponse(
            userMapper.toResponse(user),
            roleMapper.toResponse(role),
            companyMapper.toResponse(company),
            userCompanies.stream()
                .map(userCompanyMapper::toResponse)
                .toList(),
            permissions.toArray(new String[0])
        );
    }

    @Transactional(readOnly = true)
    public JwtToken refreshAccessToken(String refreshToken) {
        Session session = sessionService.requireByTokenAndRevokedFalse(refreshToken);
        if (session.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "error.auth.token.expired");
        }

        User user = userService.requireById(session.getUserId());
        UUID roleId = jwtService.extractRoleId(refreshToken);
        return jwtService.generateAccessToken(user, session.getCompanyId(), roleId);
    }

    public void logout(String refreshToken) {
        sessionService.revokeToken(refreshToken);
    }
}
