package com.jirafik.hash.service;

import com.jirafik.hash.entity.Hash;
import com.jirafik.hash.entity.PostRequest;
import com.jirafik.hash.exceptions.PostNotFoundException;
import com.jirafik.hash.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashService {

    private final HashRepository repository;

    public String postHash(PostRequest postRequest) {

        log.info("LOG: method postHash() was called.");

        Hash hash = Hash.builder()
                .hash(RandomStringUtils.random(8, "0123456789abcdefjpdkxmnoyzg"))
                .url(RandomStringUtils.random(8, "0123456789abcdefjpdkxmnoyzg"))
                .postId(postRequest.getId())
                .build();

        repository.save(hash);

        System.out.println("--Link with hash " + hash.getHash() + " was saved.");

        return hash.getUrl();
    }

    /* Method for update post download url */
    public String getPostUrl(String postId) {

        Hash hash = repository.findByPostId(postId);

        if (postId == null || hash == null)
            throw new PostNotFoundException("Post with id: " + postId + "  not found. Provide valid values and try again.");

        hash.setUrl(RandomStringUtils.random(8, "0123456789abcdefjpdkxmnoyzg"));

        return hash.getUrl();
    }

    public String getPostId(String url) {

        log.info("LOG: received url: {}", url);

        log.info("LOG: method getPostId() was called.");

        Hash hash = repository.findByUrl(url);

        if (url != null && hash != null) return hash.getPostId();

        else throw new PostNotFoundException("No post found with such parameters. Try again.");
    }

}

