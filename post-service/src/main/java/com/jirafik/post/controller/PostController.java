package com.jirafik.post.controller;

import com.jirafik.post.entity.Post;
import com.jirafik.post.entity.PostRequest;
import com.jirafik.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService service;

    @PostMapping("/send")
    public Post uploadPost(@RequestBody PostRequest request) {
        return service.upload(request);
    }

    @GetMapping("/get")
    public Object downloadPost(@RequestParam("postId") String postId) {
        return service.download(postId);
    }

    @DeleteMapping("/delete")
    public String deletePost(@RequestParam("postId") String postId) {
        return service.deletePost(postId);
    }

    @GetMapping("/files")
    public List getFileList() {
        return service.getPostList();
    }

}
