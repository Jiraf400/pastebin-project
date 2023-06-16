package com.jirafik.post.controller;

import com.jirafik.post.entity.Post;
import com.jirafik.post.entity.PostRequest;
import com.jirafik.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService service;

    @PostMapping("/send")
    public String uploadPost(@RequestBody PostRequest request) {
        return service.upload(request);
    }

    @GetMapping(value = "/get/{postUrl}", produces = {"application/json"})
    public String downloadPost(@PathVariable("postUrl") String uri) {
        return service.download(uri);
    }

    @DeleteMapping("/delete/{postId}")
    public String deletePost(@PathVariable("postId") String postId) {
        return service.deletePost(postId);
    }

    @GetMapping("/files")
    public List getFileList() {
        return service.getPostList();
    }

}
