package com.jirafik.hash.controller;

import com.jirafik.hash.entity.PostRequest;
import com.jirafik.hash.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hash")
public class HashController {

    private final HashService service;

    @PostMapping("/postHash")
    public String postHash(@RequestBody PostRequest postRequest) {
        return service.postHash(postRequest);
    }

    @GetMapping("/getUrl")
    public String getUrl(@RequestParam("postId") String postId) {
        return service.getPostUrl(postId);
    }

    @GetMapping("/getId")
    public String getPostId(@RequestParam("postUrl") String url) {
        return service.getPostId(url);
    }

}
