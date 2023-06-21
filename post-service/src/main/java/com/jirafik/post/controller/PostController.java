package com.jirafik.post.controller;

import com.jirafik.post.entity.PostRequest;
import com.jirafik.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService service;

    @GetMapping(value = "get/{postUrl}", produces = {"application/json"})
    public String downloadPost(@PathVariable("postUrl") String uri) {
        return service.download(uri);
    }

    @GetMapping("/getList")
    public List getFileList(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return service.getPostList(PageRequest.of(page, size));
    }

    @PostMapping("user/send")
    public String uploadPost(@RequestBody PostRequest request) {
        return service.upload(request);
    }

    @DeleteMapping("/user/delete/{postId}")
    public String deletePost(@PathVariable("postId") String postId) {
        return service.deletePost(postId);
    }

}
