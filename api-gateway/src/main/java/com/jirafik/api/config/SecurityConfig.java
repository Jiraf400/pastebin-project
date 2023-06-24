package com.jirafik.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHORIZATION;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {

    private final MatchPathToRoleFilter pathToRoleFilter;
    private final JwtAuthConverter jwtConverter;

    private final String[] publicPaths = {
            "/api/post/get/**",
            "/api/post/getList/**",
            "/actuator/health/**",
            "/eureka/**"
    };

    @Bean
    public SecurityWebFilterChain configureResourceServer(ServerHttpSecurity httpSecurity) {

        httpSecurity
                .authorizeExchange()
                .pathMatchers(publicPaths).permitAll()
                .anyExchange().authenticated();

        httpSecurity
                .addFilterAfter(pathToRoleFilter, AUTHORIZATION)    //filter will start after authorization step
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable();

        httpSecurity
                .oauth2ResourceServer().jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()));

        return httpSecurity.build();
    }

    //Role Converter
    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                new ReactiveJwtGrantedAuthoritiesConverterAdapter(jwtConverter));

        return jwtAuthenticationConverter;
    }

}
