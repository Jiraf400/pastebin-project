package com.jirafik.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.PostRequest;
import jdk.jfr.Frequency;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

        post.setId(String.valueOf(new Random().nextLong(10L, 100000L)));

        File fileMetadata = new File();
        fileMetadata.setName(post.getTitle() + ".json");

        try {

            java.io.File storeFile = new java.io.File("post.json");

            mapper.writeValue(storeFile, post);

            FileContent mediaContent = new FileContent("plain/txt", storeFile);

            File uploadFile = getService(googleDriveManager).files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            log.info("Post ID: " + uploadFile.getId());

        } catch (Exception e) {
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

    public String deleteFile(String fileId) throws Exception {

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

        return result.getFiles();
    }

    public Post maptoPost(PostRequest request) {
        return Post.builder()
                .title(request.getTitle())
                .img(request.getImg())
                .content(request.getContent())
                .tags(request.getTags())
                .dateOfCreation(Date.from(Instant.now()))
                .wroteBy("John")
                .build();
    }

}
