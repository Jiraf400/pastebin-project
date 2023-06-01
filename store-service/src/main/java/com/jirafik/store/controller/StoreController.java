package com.jirafik.store.controller;

import com.google.api.services.drive.model.File;
import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.PostRequest;
import com.jirafik.store.service.StoreService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService service;


    @GetMapping("/files")
    public List<File> getFileList() {
        return service.getFileList();
    }

    @PostMapping("/upload")
    public Post uploadFile(@RequestBody PostRequest postRequest) {
        return service.uploadContent(postRequest);
    }


    @GetMapping("/download")
    public void download(@RequestParam String id, HttpServletResponse response) throws IOException {
        service.downloadContent(id, response.getOutputStream());
    }

    @GetMapping("/delete")
    public String  delete(@RequestParam String id) throws Exception {
        return service.deleteFile(id);
    }

}
