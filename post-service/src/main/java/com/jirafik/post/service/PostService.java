package com.jirafik.post.service;

import com.jirafik.post.broker.consumer.RabbitConsumer;
import com.jirafik.post.broker.model.User;
import com.jirafik.post.entity.Post;
import com.jirafik.post.entity.PostRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final WebClient.Builder webClientBuilder;
    private final RabbitConsumer consumer;

    public String upload(PostRequest request) {

        request.setWroteBy(consumer.getAuthenticatedUserName());

        log.info("LOG: method upload() was called.");

        webClientBuilder.build()
                .post()
                .uri("http://store-service/api/store/upload",
                        uriBuilder -> uriBuilder.queryParam("postRequest", request).build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), PostRequest.class)
                .retrieve()
                .bodyToMono(Post.class)
                .block();

          String postUrl = webClientBuilder.build().post()
                    .uri("http://hash-service/api/hash/postHash",
                            uriBuilder -> uriBuilder.queryParam("postRequest", request).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(request), PostRequest.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        log.info("LOG: postUrl: {}", postUrl);

        return "Post placed successfully. Your post url: " + postUrl;
    }

    public String download(String postUrl) {

        log.info("LOG: method download() was called.");

        String postId = webClientBuilder.build().get()
                .uri("http://hash-service/api/hash/getId",
                        uriBuilder -> uriBuilder.queryParam("postUrl", postUrl).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("LOG: postId: {}", postId);

        String postBody = webClientBuilder.build().get()
                .uri("http://store-service/api/store/download",
                        uriBuilder -> uriBuilder.queryParam("postId", postId).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("LOG: response of download method: {}", postBody);

        return postBody;
    }

    public String deletePost(String postId) {

        log.info("LOG: method deletePost() was called.");

        webClientBuilder.build().delete()
                .uri("http://store-service/api/store/delete",
                        uriBuilder -> uriBuilder.queryParam("postId", postId).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

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

