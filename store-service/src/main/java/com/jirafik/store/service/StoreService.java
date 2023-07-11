package com.jirafik.store.service;

import com.google.gson.Gson;
import com.jirafik.store.caching.service.MetadataCacheService;
import com.jirafik.store.dto.PostRequest;
import com.jirafik.store.dto.StoredPostResponse;
import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.StoredPost;
import com.jirafik.store.exceptions.PostNotFoundException;
import com.jirafik.store.exceptions.UploadPostException;
import com.jirafik.store.repository.StoreRepository;
import com.jirafik.store.caching.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

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

    public void uploadData(PostRequest postRequest) {
        Post post = objectMapper.mapToPost(postRequest);

        byte[] file = new Gson().toJson(post).getBytes();

        InputStream is = new ByteArrayInputStream(file);

        if (postRequest.getId() != null) {
            try {
                authenticationManager.getCurrentUser()
                        .files()
                        .uploadBuilder("/" + post.getId() + ".json")
                        .uploadAndFinish(is);

            } catch (Exception e) {
                log.error("Unable to download file: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.error("Unable to download file: postRequest.getId() == null");
            throw new UploadPostException("Oops... Look like some error occurred while uploading post. Try again.");
        }

        StoredPost storedPost = objectMapper.mapToStoredPost(post);

        repository.save(storedPost);

        postCache.savePost(post);

        metadataCache.saveMetadata(storedPost);

        log.info("File was successfully saved with Id: " + post.getId());
    }

    public String downloadData(String postId) {

        Post post = postCache.fetchPostById(postId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (post == null && !postId.equals("")) {       //if redis db not contain this post we'll take it from metadata db

            StoredPost storedPost = repository.findById(postId).get();

            try {
                authenticationManager.getCurrentUser()
                        .files()
                        .downloadBuilder("/" + storedPost.getFileName())
                        .download(outputStream);

            } catch (Exception e) {
                log.error("Unable to download file: " + e.getMessage());
                e.printStackTrace();
            }

            log.info("Post body was picked from main db. Post id: {}", post.getId());

        } else {        //if redis db contains post
            log.info("Post body was successfully found in redis db. Post id: {}", post.getId());
            return post.toString();
        }

        return new JSONObject(outputStream.toString()).toString();
    }

    public void deleteData(String postId) {

        String fileToDelete = repository.findById(postId).get().getFileName();

        if (!fileToDelete.equals("")) {

            try {
                authenticationManager.getCurrentUser()
                        .files()
                        .deleteV2("/" + fileToDelete);

                metadataCache.deleteMData(postId);
                postCache.deletePost(postId);
                repository.deleteById(postId);
            } catch (Exception e) {
                log.error("Unable to delete file: " + e.getMessage());
                e.printStackTrace();
            }

            log.info("Post was successfully deleted. Post id: {}", postId);

        } else {
            log.error("Unable to delete file: Post not found");
            throw new PostNotFoundException("Oops... Look like some error occurred while deleting post. Try again.");
        }
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

        log.info("Returned list of post with {} elements", storedPostResponseList.size());

        return storedPostResponseList;
    }

}
