package com.jirafik.store.controller;

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

//    @PostMapping("/upload")
//    public void uploadData() {
//        service.uploadData();
//    }

    @GetMapping("/download")
    public String downloadData(@RequestParam("postId") String postId) {
        return service.downloadData(postId);
    }

//    @DeleteMapping("/delete")
//    public void deleteData(@RequestParam("postId") String postId) {
//        service.deleteData(postId);
//    }

    @GetMapping("/files")
    public List<StoredPostResponse> listFiles() {
        return service.getFileList();
    }

}


