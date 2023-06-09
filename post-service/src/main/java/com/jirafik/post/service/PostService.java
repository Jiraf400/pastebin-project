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

    public Post upload(PostRequest request) {

        log.info("--Method upload() was called.");

        System.out.println("PostService PostRequest.class : \n" + request);

        Post response = webClientBuilder.build().post()
                .uri("http://store-service/api/store/upload",
                        uriBuilder -> uriBuilder.queryParam("postRequest", request).build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), PostRequest.class)
                .retrieve()
                .bodyToMono(Post.class)
                .block();

        System.out.println("Response: " + response);

        return response;
    }

    public String download(String postId) {

        log.info("--Method download() was called.");

        OutputResponse response = null;
        FileOutputStream outputStream = null;
        java.io.File outputFile = null;
        String content = null;

        try {
            response = webClientBuilder.build().get()
                    .uri("http://store-service/api/store/download",
                            uriBuilder -> uriBuilder.queryParam("postId", postId).build())
                    .retrieve()
                    .bodyToMono(OutputResponse.class)
                    .block();

            outputFile = new java.io.File("temp.json");

            outputStream = new FileOutputStream(outputFile);
            outputStream.write(response.getContent());

            content = com.google.common.io.Files.asCharSource(outputFile, Charsets.UTF_8).read();

        } catch (
                Exception e) {
            System.out.println("Oops. Look like some error occurred. Failed to download post with id = " + postId +
                    ". Please try again later.\n");
            e.printStackTrace();
        }

        log.info("response of download method: {}", response.toString());

        return content;
    }


    public String deletePost(String postId) {

        return webClientBuilder.build().delete()
                .uri("http://store-service/api/store/delete",
                        uriBuilder -> uriBuilder.queryParam("postId", postId).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public List getPostList() {

        log.info("--Method getPostList() was called.");

        return webClientBuilder.build().get()
                .uri("http://store-service/api/store/files")
                .retrieve()
                .bodyToMono(List.class)
                .block();
    }


}

