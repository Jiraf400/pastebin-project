package com.jirafik.post.service;

import com.jirafik.post.dto.OutputResponse;
import com.jirafik.post.entity.Post;
import com.jirafik.post.entity.PostRequest;
import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final WebClient.Builder webClientBuilder;

    public String upload(PostRequest request) {

        log.info("LOG: method upload() was called.");

        System.out.println("PostService PostRequest.class : \n" + request);

        String postUrl = webClientBuilder.build().post()
                .uri("http://hash-service/api/hash/postHash",
                        uriBuilder -> uriBuilder.queryParam("postRequest", request).build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), PostRequest.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("LOG: postUrl: {}", postUrl);

        webClientBuilder.build()
                .post()
                .uri("http://store-service/api/store/upload",
                        uriBuilder -> uriBuilder.queryParam("postRequest", request).build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), PostRequest.class)
                .retrieve()
                .bodyToMono(Post.class)
                .block();

        return postUrl;
    }

    public String download(String postUrl) {

        log.info("LOG: method download() was called.");

        OutputResponse postBody = null;
        FileOutputStream outputStream;
        java.io.File outputFile;
        String content = null;

        String postId = "";

        try {

            postId = webClientBuilder.build().get()
                    .uri("http://hash-service/api/hash/getId",
                            uriBuilder -> uriBuilder.queryParam("postUrl", postUrl).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("LOG: postId: {}", postId);

            String finalPostId = postId;
            postBody = webClientBuilder.build().get()
                    .uri("http://store-service/api/store/download",
                            uriBuilder -> uriBuilder.queryParam("postId", finalPostId).build())
                    .retrieve()
                    .bodyToMono(OutputResponse.class)
                    .block();

            outputFile = new java.io.File("temp.json");

            outputStream = new FileOutputStream(outputFile);
            outputStream.write(postBody.getContent());

            content = com.google.common.io.Files.asCharSource(outputFile, Charsets.UTF_8).read();

        } catch (Exception e) {
            e.printStackTrace();

            return "Oops. Look like some error occurred. Failed to download post with id = " + postId +
                    ". Please try again later.\n";
        }

        log.info("LOG: response of download method: {}", postBody.toString());

        return content;
    }

    public String deletePost(String postId) {

        log.info("LOG: method deletePost() was called.");

        return webClientBuilder.build().delete()
                .uri("http://store-service/api/store/delete",
                        uriBuilder -> uriBuilder.queryParam("postId", postId).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public List getPostList() {

        log.info("LOG: method getPostList() was called.");

        return webClientBuilder.build().get()
                .uri("http://store-service/api/store/files")
                .retrieve()
                .bodyToMono(List.class)
                .block();
    }


}

