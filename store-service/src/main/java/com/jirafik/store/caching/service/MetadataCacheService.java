package com.jirafik.store.caching.service;

import com.jirafik.store.caching.repository.MetadataRepository;
import com.jirafik.store.entity.StoredPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetadataCacheService {

    private final MetadataRepository repository;

    public void saveMetadata(StoredPost post) {
        repository.saveToCache(post);
    }

    public List<StoredPost> fetchAllPosts() {
        return repository.fetchAllFromCache();
    }

    public StoredPost fetchMDataById(String postId) {
        return repository.fetchById(postId);
    }

    public boolean deleteMData(String postId) {
        return repository.deleteFromCache(postId);
    }

}
