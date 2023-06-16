package com.jirafik.store.service;

import com.dropbox.core.v2.files.FileMetadata;
import com.google.gson.Gson;
import com.jirafik.store.dto.PostRequest;
import com.jirafik.store.dto.StoredPostResponse;
import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.StoredPost;
import com.jirafik.store.exceptions.DownloadPostException;
import com.jirafik.store.exceptions.PostNotFoundException;
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

        List<StoredPostResponse> storedPostResponseList = new ArrayList<>();

        for (StoredPost post : repository.findAll()) {
            storedPostResponseList.add(mapToStoredPostResponse(post));
        }

        if (storedPostResponseList.size() == 0) {
            return List.of(new StoredPostResponse("WARN", "No posts found"));
        }

        return storedPostResponseList;
    }

    public void uploadData(PostRequest postRequest) {

        Post post = maptoPost(postRequest);

        byte[] file = new Gson().toJson(post).getBytes();

        InputStream is = new ByteArrayInputStream(file);

        FileMetadata uploadedPost = null;

        if (postRequest.getId() != null) {

            try {

                uploadedPost = authenticationManager.getCurrentUser()
                        .files()
                        .uploadBuilder("/" + post.getId() + ".json")
                        .uploadAndFinish(is);


            } catch (Exception e) {
                log.info("Unable to download file: " + e.getMessage());
                e.printStackTrace();
            }
        } else
            throw new UploadPostException("Oops... Look like some error occurred while uploading post. Try again.");

        StoredPost storedPost = StoredPost.builder()
                .id(post.getId())
                .postTitle(post.getTitle())
                .fileName(uploadedPost.getName())
                .build();

        repository.save(storedPost);

        log.info("LOG: File was successfully saved with Id: " + post.getId());
    }

    public String downloadData(String postId) {

        StoredPost post = repository.findById(postId).get();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (post.getId() != null && !postId.equals("")) {

            try {

                authenticationManager.getCurrentUser()
                        .files()
                        .downloadBuilder("/" + post.getFileName())
                        .download(outputStream);


            } catch (Exception e) {
                log.info("Unable to download file: " + e.getMessage());
                e.printStackTrace();
            }
        } else
            throw new DownloadPostException("Oops... Look like some error occurred while downloading post. Try again.");

        System.out.println("ByteArrayOutputStream outputStream: " + outputStream);

        return new JSONObject(outputStream.toString()).toString();
    }

    public String deleteData(String postId) {

        StoredPost post = repository.findById(postId).get();

        String fileToDelete = post.getFileName();

        if (!post.getFileName().equals("")) {

            try {
                authenticationManager.getCurrentUser()
                        .files()
                        .deleteV2("/" + fileToDelete);

                repository.deleteById(postId);
            } catch (Exception e) {
                log.info("Unable to download file: " + e.getMessage());
                e.printStackTrace();
            }

        } else throw new PostNotFoundException("Oops... Look like some error occurred while deleting post. Try again.");

        return "File with name: " + fileToDelete + " was deleted.";
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

    private StoredPostResponse mapToStoredPostResponse(StoredPost storedPost) {
        return StoredPostResponse.builder()
                .id(storedPost.getId())
                .postTitle(storedPost.getPostTitle())
                .build();
    }

}
