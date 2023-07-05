package com.jirafik.post.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    private final String[] publicPaths = {
            "/api/post/get/**",
            "/api/post/getList/**",
            "/actuator/health/**",
            "/eureka/**"
    };

    @Bean
    public SecurityFilterChain configureResourceServer(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests()
                .requestMatchers(publicPaths).permitAll()
                .anyRequest()
                .authenticated();
        httpSecurity
                .csrf().disable();

        httpSecurity
                .oauth2ResourceServer().jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter));

        return httpSecurity.build();
    }


}
