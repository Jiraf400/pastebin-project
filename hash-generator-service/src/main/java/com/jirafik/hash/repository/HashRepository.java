package com.jirafik.hash.repository;

import com.jirafik.hash.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashRepository extends JpaRepository<Hash, Integer> {

    Hash findByPostId(String id);
    Hash findByUrl(String url);

}
