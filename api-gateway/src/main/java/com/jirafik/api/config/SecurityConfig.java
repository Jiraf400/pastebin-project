package com.jirafik.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHORIZATION;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {

    private final MatchPathToRoleFilter pathToRoleFilter;

    private final String[] allowedPaths = {
            "/api/post/get/**",
            "/api/post/getList/**",
            "/actuator/health/**",
            "/eureka/**"
    };

    @Bean
    public SecurityWebFilterChain configureResourceServer(ServerHttpSecurity httpSecurity) {

        httpSecurity
                .authorizeExchange()
                .pathMatchers(allowedPaths).permitAll()
//                .pathMatchers("/api/post/user/delete/**", "/api/post/user/send/**").hasRole("USER")
                .anyExchange().authenticated();

        httpSecurity
                .addFilterBefore(pathToRoleFilter, AUTHORIZATION)
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable();

        httpSecurity
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);

        return httpSecurity.build();
    }

}
