package com.jirafik.api.config;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MatchPathToRoleFilter implements WebFilter {

    @Value("${security.access.path.user}")
    private String userPath;
    @Value("${security.access.role.user}")
    private String userName;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal()
                .flatMap(principal -> checkPathForRoleAccess(exchange, chain, principal))
                .switchIfEmpty(chain.filter(exchange))
                .onErrorStop();
    }

    Mono<Void> checkPathForRoleAccess(ServerWebExchange exchange, WebFilterChain chain, Principal principal) {

        var paths = extractPathsFromRequest(exchange.getRequest().getPath().value());
        var requestRoles = extractRoles(principal);

        if (hasProblemForRole(paths, requestRoles, userPath, userName)) {

            return Mono.error(() -> new AccessDeniedException("Request rejected: access denied"));
        }

        return chain.filter(exchange);
    }

    private Set<String> extractPathsFromRequest(String fullPath) {
        return Arrays.stream(fullPath.split("/"))
                .collect(Collectors.toSet());
    }

    private Set<String> extractRoles(Principal principal) {
        return ((JwtAuthenticationToken) principal).getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(roleName -> roleName.substring(5))
                .collect(Collectors.toSet());
    }

    private boolean hasProblemForRole(Set<String> pathsSet, Set<String> requestRoles, String rolePath, String roleName) {
        return pathsSet.contains(rolePath) && !requestRoles.contains(roleName);
    }

}