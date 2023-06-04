package com.jirafik.hash.service;

import com.jirafik.hash.entity.Link;
import com.jirafik.hash.entity.PostRequest;
import com.jirafik.hash.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class HashService {

    private final HashRepository repository;

    public void postLink(PostRequest postRequest) {
        repository.save(mapToLink(postRequest));
    }

    private Link mapToLink(PostRequest post) {

        return Link.builder()
                .isExpired(new Date().compareTo(getExpirationDate()) < 0)
                .hash(UUID.randomUUID().toString().replace("-", "").substring(1, 8))
                .build();
    }

    public static void main(String[] args) {

        Link link1 = Link.builder()
                .isExpired(new Date().equals(Instant.now()))
                .hash(UUID.randomUUID().toString().replace("-", "").substring(0, 8))
                .build();

        System.out.println(link1.getHash());

    }

    public static long getUniqueLongFromString(String value) {
        return UUID.nameUUIDFromBytes(value.getBytes()).getMostSignificantBits();
    }

    private Date getExpirationDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    //Post.class (id)
    public String getLink(String id) {

        Link link = repository.findLinkByPostID(id);

        if (link != null) {

            return link.getHash();

        }

        return "Oops. Some error occurs. Please check if request was valid and try again";
    }


}
