package com.jirafik.store.controller;

import com.google.api.services.drive.model.File;
import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.PostRequest;
import com.jirafik.store.entity.StoredPostResponse;
import com.jirafik.store.service.StoreService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/download")
    public OutputStream download(@RequestParam("postID") String id, HttpServletResponse response) throws IOException {
        return service.downloadContent(id, response.getOutputStream());
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam("postID") String id) throws Exception {
        return service.deleteContent(id);
    }

}
