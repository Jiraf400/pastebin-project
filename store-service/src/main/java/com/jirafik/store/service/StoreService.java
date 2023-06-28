package com.jirafik.store.service;

import com.google.gson.Gson;
import com.jirafik.store.caching.service.MetadataCacheService;
import com.jirafik.store.dto.PostRequest;
import com.jirafik.store.dto.StoredPostResponse;
import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.StoredPost;
import com.jirafik.store.exceptions.DownloadPostException;
import com.jirafik.store.exceptions.PostNotFoundException;
import com.jirafik.store.exceptions.UploadPostException;
import com.jirafik.store.repository.StoreRepository;
import com.jirafik.store.caching.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreService {

    private final DropBoxAuthenticationManager authenticationManager;
    private final StoreRepository repository;           //postgres db for metadata

    private final PostCacheService postCache;           //redis post caching
    private final MetadataCacheService metadataCache;   //redis post metadata caching

    private final ObjectMapper objectMapper;

    @Transactional
    public void uploadData(PostRequest postRequest) {

        log.info("uploadData method started.");

        System.out.println("PostRequest : " + postRequest);

        Post post = objectMapper.maptoPost(postRequest);
        byte[] file = new Gson().toJson(post).getBytes();
        InputStream is = new ByteArrayInputStream(file);

        System.out.println();

        System.out.println("Post : " + post);


        if (postRequest.getId() != null) {
            try {
                authenticationManager.getCurrentUser()
                        .files()
                        .uploadBuilder("/" + post.getId() + ".json")
                        .uploadAndFinish(is);

            } catch (Exception e) {
                log.info("Unable to download file: " + e.getMessage());
                e.printStackTrace();
            }
        } else
            throw new UploadPostException("Oops... Look like some error occurred while uploading post. Try again.");

        StoredPost storedPost = objectMapper.mapToStoredPost(post);

        repository.save(storedPost);

        postCache.savePost(post);

//        metadataCache.saveMetadata(storedPost);

        log.info("LOG: File was successfully saved with Id: " + post.getId());
    }

    public String downloadData(String postId) {

        StoredPost post = metadataCache.fetchMDataById(postId);

        if (post == null) {
            System.out.println("IF-block in downloadData() method was picked.");
            post = repository.findById(postId).get();
            System.out.println("post : " + post);
        }

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

        StoredPost post;

        post = metadataCache.fetchMDataById(postId);

        //TODO exception handler if post not found

        if (post == null) {
            post = repository.findById(postId).get();
            System.out.println("IF-block in deleteData() method was picked.");
        }

        String fileToDelete = post.getFileName();

        if (!post.getFileName().equals("")) {

            try {
                authenticationManager.getCurrentUser()
                        .files()
                        .deleteV2("/" + fileToDelete);

                metadataCache.deleteMData(postId);
                postCache.deletePost(postId);
                repository.deleteById(postId);
            } catch (Exception e) {
                log.info("Unable to delete file: " + e.getMessage());
                e.printStackTrace();
            }

        } else throw new PostNotFoundException("Oops... Look like some error occurred while deleting post. Try again.");

        return "File with name: " + fileToDelete + " was deleted.";
    }

    public List<StoredPostResponse> getFileList() {

        List<StoredPostResponse> storedPostResponseList = new ArrayList<>();

        //bring Post metadata from Redis
        List<StoredPost> metaList = metadataCache.fetchAllPosts();

        if (metaList.size() > 0) {

            for (StoredPost post : metaList)

                storedPostResponseList.add(objectMapper.mapToStoredPostResponse(post));

        } else {    // if metadata list from Redis is empty
            metaList = repository.findAll();

            for (StoredPost post : metaList)

                storedPostResponseList.add(objectMapper.mapToStoredPostResponse(post));
        }

        if (storedPostResponseList.size() == 0)
            return List.of(new StoredPostResponse("WARN", "No posts found"));

        return storedPostResponseList;
    }

}
