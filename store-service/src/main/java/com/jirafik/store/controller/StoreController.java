package com.jirafik.store.controller;

import com.jirafik.store.dto.OutputResponse;
import com.jirafik.store.entity.Post;
import com.jirafik.store.dto.PostRequest;
import com.jirafik.store.dto.StoredPostResponse;
import com.jirafik.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService service;

    @GetMapping("/files")
    public List<StoredPostResponse> getFileList() {
        return service.getPostList();
    }

    @PostMapping("/upload")
    public Post uploadFile(@RequestBody PostRequest postRequest) {
        return service.uploadContent(postRequest);
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_JSON_VALUE)
    public OutputResponse download(@RequestParam("postId") String id) {
        return service.downloadContent(id);
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam("postId") String id) {
        return service.deleteContent(id);
    }

}


