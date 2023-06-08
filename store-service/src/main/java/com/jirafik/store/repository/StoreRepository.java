package com.jirafik.store.repository;

import com.jirafik.store.entity.StoredPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<StoredPost, String> {

    Optional<StoredPost> findById(String id);

    void deleteById(String id);

}

