package com.jirafik.post.service;

import com.jirafik.post.broker.producer.PostProducer;
import com.jirafik.post.entity.PostRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Data
@RequiredArgsConstructor
@Service
public class PostService {

    private final WebClient.Builder webClientBuilder;
    private String wroteByName;
    private final PostProducer producer;

    @SneakyThrows
    public String upload(PostRequest request) {

        request.setWroteBy(getWroteByName());

        log.info("LOG: method upload() was called.");
        String postUrl = "";

        try {
            producer.sendUploadRequest(request);

            postUrl = webClientBuilder.build().post()
                    .uri("http://hash-service/api/hash/postHash",
                            uriBuilder -> uriBuilder.queryParam("postRequest", request).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(request), PostRequest.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            e.printStackTrace();
            return "Cannot upload post. Try again later.";
        }

        log.info("LOG: Post placed successfully: {}", postUrl);

        return "Post placed successfully. Download hash: " + postUrl;
    }

    public String download(String postUrl) {

        log.info("LOG: method download() was called.");

        String postBody;

        try {
            String postId = webClientBuilder.build().get()
                    .uri("http://hash-service/api/hash/getId",
                            uriBuilder -> uriBuilder.queryParam("postUrl", postUrl).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("LOG: postId: {}", postId);

            postBody = webClientBuilder.build().get()
                    .uri("http://store-service/api/store/download",
                            uriBuilder -> uriBuilder.queryParam("postId", postId).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return "Cannot download post. Try again later.";
        }

        log.info("LOG: response of download method: {}", postBody);

        return postBody;
    }

    public String deletePost(String postId) {

        log.info("LOG: method deletePost() was called.");

        try {
            producer.sendDeleteRequest(PostRequest.builder().id(postId).build());
        } catch (Exception e) {
            e.printStackTrace();
            return "Cannot delete post. Try again later.";
        }
        return "Post with id: " + postId + " was successfully deleted";
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

