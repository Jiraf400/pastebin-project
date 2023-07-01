package com.jirafik.post.controller;

import com.jirafik.post.entity.PostRequest;
import com.jirafik.post.service.PostService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService service;

    @GetMapping(value = "get/{postUrl}", produces = {"application/json"})
    @CircuitBreaker(name = "storeService")
    @TimeLimiter(name = "storeService")
    @Retry(name = "storeService")
    public CompletableFuture<String> downloadPost(@PathVariable("postUrl") String uri) {
        return CompletableFuture.supplyAsync(() -> service.download(uri));
    }

    @GetMapping("/getList")
    @CircuitBreaker(name = "storeService")
    @TimeLimiter(name = "storeService")
    @Retry(name = "storeService")
    public CompletableFuture<List> getPostList() {
        return CompletableFuture.supplyAsync(service::getPostList);
    }

    @PostMapping("user/send")
    @CircuitBreaker(name = "storeService")
    @TimeLimiter(name = "storeService")
    @Retry(name = "storeService")
    public CompletableFuture<String> uploadPost(@RequestBody PostRequest request) {
        return CompletableFuture.supplyAsync(() -> service.upload(request));
    }

    @DeleteMapping("/user/delete/{postId}")
    @CircuitBreaker(name = "storeService")
    @TimeLimiter(name = "storeService")
    @Retry(name = "storeService")
    public CompletableFuture<String> deletePost(@PathVariable("postId") String postId) {
        return CompletableFuture.supplyAsync(() -> service.deletePost(postId));
    }

}
