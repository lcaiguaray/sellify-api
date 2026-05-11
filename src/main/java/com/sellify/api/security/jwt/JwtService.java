package com.sellify.api.security.jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.sellify.api.modules.auth.domain.User;
import com.sellify.api.modules.auth.dto.JwtToken;
import com.sellify.api.modules.auth.repository.RoleRepository;
import com.sellify.api.security.config.JwtProperties;
import com.sellify.api.security.domain.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private final RoleRepository roleRepository;

    public JwtToken buildToken(User user, Map<String, Object> extraClaims, long expirationSeconds) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(issuedAt.getTime() + expirationSeconds * 1000L);

        String token = Jwts.builder()
            .claims(extraClaims)
            .subject(user.getId().toString())
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(getSignInKey())
            .compact();

        return new JwtToken(token, issuedAt, expiration);
    }

    public JwtToken generateAccessToken(User user, UUID companyId, UUID roleId) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(jwtProperties.claims().companyId(), companyId);
        if (roleId != null) {
            extraClaims.put(jwtProperties.claims().roleId(), roleId);
        }
        
        return buildToken(user, extraClaims, jwtProperties.expiration());
    }

    public JwtToken generateRefreshToken(User user, UUID companyId, UUID roleId) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(jwtProperties.claims().companyId(), companyId);
        if (roleId != null) {
            extraClaims.put(jwtProperties.claims().roleId(), roleId);
        }

        return buildToken(user, extraClaims, jwtProperties.refreshExpiration());
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public UUID extractRoleId(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

            return Optional.ofNullable(claims.get(jwtProperties.claims().roleId()))
                .map(Object::toString)
                .map(UUID::fromString)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();

        UUID roleId = extractRoleId(token);
        List<String> permissionIds = (roleId != null) 
            ? roleRepository.findPermissionIdsByRoleId(roleId).stream().collect(Collectors.toList())
            : Collections.emptyList();
        
        Collection<GrantedAuthority> authorities = permissionIds.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        UUID userId = UUID.fromString(claims.getSubject());
        UUID companyId = UUID.fromString(claims.get(jwtProperties.claims().companyId(), String.class));
        return new UsernamePasswordAuthenticationToken(new UserPrincipal(userId, companyId, roleId), null, authorities);
    }

    public Date extractExpiration(String token) {
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
