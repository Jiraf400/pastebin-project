package com.jirafik.store.controller;

import com.google.api.services.drive.model.File;
import com.jirafik.store.entity.Post;
import com.jirafik.store.dto.PostRequest;
import com.jirafik.store.dto.StoredPostResponse;
import com.jirafik.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public File download(@RequestParam("postId") String id){
        return service.downloadContent(id);
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam("postId") String id) {
        return service.deleteContent(id);
    }

}
