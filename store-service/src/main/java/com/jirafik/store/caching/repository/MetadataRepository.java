package com.jirafik.store.caching.repository;

import com.jirafik.store.entity.StoredPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MetadataRepository{

    private final RedisTemplate redisTemplate;

    private static final String KEY = "POST_METADATA";

    public boolean saveToCache(StoredPost post) {
        System.out.println("MetadataRepository.saveToCache() started");
        try {
            redisTemplate.opsForHash().put(KEY, post.getId(), post);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<StoredPost> fetchAllFromCache() {
        return redisTemplate.opsForHash().values(KEY);
    }

    public StoredPost fetchById(String id) {
        return (StoredPost) redisTemplate.opsForHash().get(KEY, id);
    }

    public boolean deleteFromCache(String id) {
        try {
            redisTemplate.opsForHash().delete(KEY, id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
