package com.jirafik.api.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtAuthConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        return ((ArrayList<String>) source.getClaims().get("roles"))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}