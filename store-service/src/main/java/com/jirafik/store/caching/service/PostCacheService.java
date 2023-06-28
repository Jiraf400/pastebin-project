package com.jirafik.store.caching.service;

import com.jirafik.store.caching.repository.PostRepository;
import com.jirafik.store.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final PostRepository repository;

    public void savePost(Post post) {
        repository.saveToCache(post);
    }

    public List<Post> fetchAllPosts() {
        return (List<Post>) repository.fetchAllFromCache();
    }

    public Post fetchPostById(String postId) {
        return (Post) repository.fetchById(postId);
    }

    public boolean deletePost(String postId) {
        return repository.deleteFromCache(postId);
    }
}
