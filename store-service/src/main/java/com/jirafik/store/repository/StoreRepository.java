package com.jirafik.store.repository;

import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.StoredPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<StoredPost, String> {


    String findFileById(String id);



}

