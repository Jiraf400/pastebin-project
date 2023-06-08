package com.jirafik.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.jirafik.store.entity.Post;
import com.jirafik.store.dto.PostRequest;
import com.jirafik.store.entity.StoredPost;
import com.jirafik.store.dto.StoredPostResponse;
import com.jirafik.store.exceptions.DownloadPostException;
import com.jirafik.store.exceptions.PostNotFound;
import com.jirafik.store.exceptions.UploadPostException;
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
            java.io.File storeFile = new java.io.File("post.json");

            mapper.writeValue(storeFile, post);

            FileContent mediaContent = new FileContent("plain/txt", storeFile);

            var uploadFile = getService(googleDriveManager).files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            if (uploadFile != null) {
                log.info("File ID: " + uploadFile.getId());

                StoredPost storedPost = StoredPost.builder()
                        .id(post.getId())
                        .postTitle(post.getTitle())
                        .fileId(uploadFile.getId())
                        .build();

                repository.save(storedPost);
            } else
                throw new UploadPostException("Oops... Look like some error occurred while uploading post. Try again.");


        } catch (UploadPostException e) {
            log.info("Unable to upload file: " + e.getMessage());
        } catch (Exception e) {
            log.info("Unable to upload file: " + e.getMessage());
        }

        return post;
    }

    public File downloadContent(String postId) {

        StoredPost storedPost = repository.findById(postId).get();

        HttpResponse httpResponse = null;

        String fileId = storedPost.getFileId();

        //TODO
        //OutputStream outputStream = new ByteArrayOutputStream();
        //
        //      service.files().get(realFileId)
        //          .executeMediaAndDownloadTo(outputStream);
        //
        //      return (ByteArrayOutputStream) outputStream;

        try {

            System.out.println("storedPost: " + storedPost);
            System.out.println("fileId: " + fileId);

            if (storedPost.getId() != null && !fileId.equals("")) {

                httpResponse = getService(googleDriveManager)
                        .files()
                        .export(fileId, "plain/text")
                        .executeMedia();

                Drive.Files.Get get = getService(googleDriveManager).files().get(fileId);
                httpResponse = get.executeMedia();

                System.out.println("Response: " + httpResponse);
//                System.out.println("post: " + post);

            } else
                throw new DownloadPostException("Oops... Look like some error occurred while downloading post. Try again.");

        } catch (DownloadPostException e) {
            System.err.println("Unable to download file with id: " + fileId + "\n" + e.getMessage());
        } catch (Exception e) {
            log.info("Unable to download file: " + e.getMessage());
            e.printStackTrace();
        }

        return null;

    }

    public String deleteContent(String postId) {

        System.out.println("--Start method deleteContent(postId)");
        StoredPost storedPost = repository.findById(postId).get();
        String fileToDelete = storedPost.getFileId();

        try {

            if (storedPost.getId() != null && !fileToDelete.equals("")) {

                getService(googleDriveManager)
                        .files()
                        .delete(fileToDelete)
                        .execute();

            } else throw new PostNotFound("Oops... Look like some error occurred while deleting post. Try again.");

        } catch (PostNotFound e) {
            System.err.println("Unable to download file with id: " + fileToDelete + "\n" + e.getMessage());
        } catch (Exception e) {
            log.info("Unable to delete file: " + e.getMessage());
        }

        repository.deleteById(postId);

        return "Post with ID :" + postId + " was deleted";
    }

    public List<StoredPostResponse> getPostList() {

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

//    private String decodePostID(String postID) {
//        byte[] decodedBytes = Base64.getDecoder().decode(postID);
//        return new String(decodedBytes);
//    }
//
//    private String encodePostId(String postID) {
//        return  Base64.getEncoder().encodeToString(postID.getBytes());
//    }

}
