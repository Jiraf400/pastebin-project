package com.jirafik.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.PostRequest;
import com.jirafik.store.entity.StoredPost;
import com.jirafik.store.entity.StoredPostResponse;
import com.jirafik.store.repository.StoreRepository;
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
    private final StoreRepository repository;

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

            var uploadFile = getService(googleDriveManager).files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            log.info("File ID: " + uploadFile.getId());

            StoredPost storedPost = StoredPost.builder()
                    .id(post.getId())
                    .postTitle(post.getTitle())
                    .fileId(uploadFile.getId())
                    .build();

            repository.save(storedPost);

        } catch (
                Exception e) {
            log.info("Unable to upload file: " + e.getMessage());
        }

        return post;
    }


    public OutputStream downloadContent(String postId, OutputStream outputStream) {

        byte[] decodedBytes = Base64.getDecoder().decode(postId);
        String decodedPostId = new String(decodedBytes);

        String fileID = repository.findFileById(decodedPostId);

        try {

            getService(googleDriveManager)
                    .files()
                    .get(fileID)
                    .executeMediaAndDownloadTo(outputStream);

        } catch (Exception e) {
            System.err.println("Unable to move file: " + e.getMessage());
        }

        return outputStream;

    }

    public String deleteContent(String fileId) throws Exception {

        getService(googleDriveManager)
                .files()
                .delete(fileId)
                .execute();

        return "File with ID :" + fileId + " was deleted";
    }


    /*

   {
        "id: "...",
        "title": "..."
   },
   {
        "id: "...",
        "title": "..."
   },
   {
        "id: "...",
        "title": "..."
   },
   ***

   */
    public List<StoredPostResponse> getPostList() {

        List<StoredPost> storedPostList = repository.findAll();

        List<StoredPostResponse> postResponseList = new ArrayList<>();

        for (StoredPost post : storedPostList) {

            for(StoredPostResponse response : postResponseList) {
                response.setId(post.getId());
                response.setPostTitle(post.getPostTitle());
                postResponseList.add(response);

            }
        }

        return postResponseList;

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


    public static void main(String[] args) {
        String key = "894735894789527";

        String encodedString = Base64.getEncoder().encodeToString(key.getBytes());

        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);

        System.out.println(encodedString);
        System.out.println();
        System.out.println(decodedString);


    }

}
