package com.jirafik.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.PostRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreService {
    private final GoogleDriveManager googleDriveManager = new GoogleDriveManager();

    private Drive getService(GoogleDriveManager manager) throws GeneralSecurityException, IOException {
        return manager.getInstance();
    }

    public Post uploadContent(PostRequest request) {

        ObjectMapper mapper = new ObjectMapper();

        Post post = maptoPost(request);

        File fileMetadata = new File();
        fileMetadata.setName(post.getTitle() + ".json");

        try {

            System.out.println("StoreService Post.class : \n" + post);
            System.out.println();
            System.out.println("StoreService PostRequest.class : \n" + request);

            java.io.File storeFile = new java.io.File("post.json");

            mapper.writeValue(storeFile, post);

            FileContent mediaContent = new FileContent("plain/txt", storeFile);

            File uploadFile = getService(googleDriveManager).files().create(fileMetadata, mediaContent)
                    .setFields("id").set(post.getId(), Post.class).execute();

            log.info("Post ID: " + uploadFile.getId());

        } catch (
                Exception e) {
            log.info("Unable to upload file: " + e.getMessage());
        }

        return post;
    }


    public void downloadContent(String fileId, OutputStream outputStream) {

        System.out.println("Download method: \n caught realFileId: " + fileId);

        try {

            googleDriveManager.getInstance()
                    .files()
                    .get(fileId)
                    .executeMediaAndDownloadTo(outputStream);

        } catch (Exception e) {
            System.err.println("Unable to move file: " + e.getMessage());
        }

    }

    public String deleteContent(String fileId) throws Exception {

        getService(googleDriveManager)
                .files()
                .delete(fileId)
                .execute();

        return "File with ID :" + fileId + " was deleted";
    }

    public List<File> getFileList() {

        FileList result = null;

        try {
            result = getService(googleDriveManager).files().list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result != null ? result.getFiles() : null;
    }

    public Post maptoPost(PostRequest request) {
        return Post.builder()
                .id(request.getId())
                .title(request.getTitle())
                .img(request.getImg())
                .content(request.getContent())
                .tags(request.getTags())
                .dateOfCreation(Date.from(Instant.now()))
                .wroteBy("John")
                .build();
    }

}
