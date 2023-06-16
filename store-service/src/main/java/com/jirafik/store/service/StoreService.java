package com.jirafik.store.service;

import com.dropbox.core.v2.files.FileMetadata;
import com.google.gson.Gson;
import com.jirafik.store.dto.PostRequest;
import com.jirafik.store.dto.StoredPostResponse;
import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.StoredPost;
import com.jirafik.store.exceptions.DownloadPostException;
import com.jirafik.store.exceptions.PostNotFound;
import com.jirafik.store.exceptions.UploadPostException;
import com.jirafik.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Instant;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreService {

    private final DropBoxAuthenticationManager authenticationManager;

    private final StoreRepository repository;

    public List<StoredPostResponse> getFileList() {

        List<StoredPost> storedPostList = repository.findAll();

        List<StoredPostResponse> postResponseList = new ArrayList<>();

        if (storedPostList.size() != 0) {

            for (StoredPost post : storedPostList) {

                StoredPostResponse response = StoredPostResponse.builder()
                        .id(post.getId())
                        .postTitle(post.getPostTitle())
                        .build();

                postResponseList.add(response);
            }

        } else return List.of(new StoredPostResponse("WARN", "No posts found in storage."));

        return postResponseList;
    }

    public void uploadData(PostRequest request) {

        Post post = maptoPost(request);

        String jsonObj = new Gson().toJson(post);

        InputStream is = new ByteArrayInputStream(jsonObj.getBytes());

        try {
            FileMetadata uploaded = authenticationManager.getCurrentUser()
                    .files()
                    .uploadBuilder("/" + request.getTitle() + ".json")
                    .uploadAndFinish(is);

            if (uploaded != null) {
                log.info("File ID: " + uploaded.getId());

                StoredPost storedPost = StoredPost.builder()
                        .id(post.getId())
                        .fileName(uploaded.getName())
                        .postTitle(post.getTitle())
                        .build();

                repository.save(storedPost);
            } else
                throw new UploadPostException("Oops... Look like some error occurred while uploading post. Try again.");

        } catch (Exception e) {
            log.info("Unable to upload file: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public String downloadData(String postId) {

        StoredPost storedPost = repository.findById(postId).get();

        String fileName = storedPost.getFileName();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {

            if (storedPost.getId() != null && !fileName.equals("")) {

                authenticationManager.getCurrentUser()
                        .files()
                        .downloadBuilder("/" + fileName)
                        .download(outputStream);

            } else
                throw new DownloadPostException("Oops... Look like some error occurred while downloading post. Try again.");

        } catch (Exception e) {
            log.info("Unable to download file: " + e.getMessage());
            e.printStackTrace();
        }
        return new JSONObject(outputStream.toString()).toString();
    }

    public String deleteData(String postId) {

        StoredPost storedPost = repository.findById(postId).get();
        String fileNameToDelete = storedPost.getFileName();

        try {

            if (storedPost.getId() != null && !fileNameToDelete.equals("")) {

                authenticationManager.getCurrentUser()
                        .files()
                        .deleteV2("/" + fileNameToDelete);

            } else
                throw new PostNotFound("Oops... Look like some error occurred while deleting post. Try again.");

        } catch (Exception e) {
            log.info("Unable to delete file: " + e.getMessage());
            e.printStackTrace();
        }

        repository.deleteById(postId);

        return "File with name: " + fileNameToDelete + " was deleted.";

    }

    private Post maptoPost(PostRequest request) {
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
