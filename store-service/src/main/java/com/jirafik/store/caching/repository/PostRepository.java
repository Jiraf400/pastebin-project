package com.jirafik.store.caching.repository;

import com.jirafik.store.entity.Post;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class PostRepository{

    private RedisTemplate redisTemplate;

    private static final String KEY = "POST";

    public void saveToCache(Post post) {
        System.out.println("PostRepository.saveToCache() started");
        try {
            redisTemplate.opsForHash().put(KEY, post.getId().toString(), post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Post> fetchAllFromCache() {
        return redisTemplate.opsForHash().values(KEY);
    }

    public Post fetchById(String id) {
        return (Post) redisTemplate.opsForHash().get(KEY, id);
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
