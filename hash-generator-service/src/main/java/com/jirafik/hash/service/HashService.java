package com.jirafik.hash.service;

import com.jirafik.hash.entity.Link;
import com.jirafik.hash.entity.PostRequest;
import com.jirafik.hash.repository.HashRepository;
import com.netflix.discovery.converters.Auto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class HashService {

    private final HashRepository repository;

    public String postHash(PostRequest postRequest) {

        Link link = Link.builder()
                .isExpired(new Date().compareTo(getExpirationDate()) < 0)
                .hash(Base64.getEncoder().encodeToString(postRequest.getId().getBytes()))
                .build();

        repository.save(link);

        return link.getHash();
    }

    private Date getExpirationDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }


}
