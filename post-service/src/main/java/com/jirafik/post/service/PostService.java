package com.jirafik.post.service;

import com.jirafik.post.entity.Post;
import com.jirafik.post.entity.PostRequest;
import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.OutputStream;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final WebClient.Builder webClientBuilder;

    public Post upload(PostRequest request) {

        log.info("--Method upload() was called.");
//        String encodedPostID = webClientBuilder.build().post()
//                .uri("http://hash-service/api/hash/postHash",
//                        uriBuilder -> uriBuilder.queryParam("postRequest", request).build())
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(Mono.just(request), PostRequest.class)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        request.setId(encodedPostID);

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

    public Object download(String postId) {

        log.info("--Method download() was called.");

        Object response = null;

        try {
            response = webClientBuilder.build().get()
                    .uri("http://store-service/api/store/download",
                            uriBuilder -> uriBuilder.queryParam("postId", postId).build())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

        } catch (Exception e) {
            System.out.println("Oops. Look like some error occurred. Failed to download post with id = " + postId +
                    ". Please try again later.\n");
            e.printStackTrace();
        }

        log.info("response of download method: {}", response);

        return response;
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

