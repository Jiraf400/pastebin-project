package com.jirafik.post.service;

import com.jirafik.post.broker.producer.PostProducer;
import com.jirafik.post.entity.PostRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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

    public String upload(PostRequest request) {

        request.setWroteBy(getWroteByName());

        String postUrl = "";
        String postId = request.getId();

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
            log.error("Cannot upload post with id: {}", postId);
            e.printStackTrace();
            return "Cannot upload post. Try again later.";
        }

        log.info("Post was placed successfully: {}", postId);

        return "Post was placed successfully. Download hash: " + postUrl;
    }

    public String download(String postUrl) {

        String postBody;
        String postId;

        try {
            postId = webClientBuilder.build().get()
                    .uri("http://hash-service/api/hash/getId",
                            uriBuilder -> uriBuilder.queryParam("postUrl", postUrl).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            postBody = webClientBuilder.build().get()
                    .uri("http://store-service/api/store/download",
                            uriBuilder -> uriBuilder.queryParam("postId", postId).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            log.error("Cannot download post with postUrl: {}", postUrl);
            e.printStackTrace();
            return "Failed to get post. Please provide correct url and try again.";
        }

        log.info("Post was downloaded successfully.");
        return postBody;
    }

    public String deletePost(String postId) {

        try {
            producer.sendDeleteRequest(PostRequest.builder().id(postId).build());
        } catch (Exception e) {
            log.error("Cannot delete post with id: {}", postId);
            e.printStackTrace();
            return "Cannot delete post. Try again later.";
        }
        log.info("Post was deleted successfully. Post id: " + postId);

        return "Post with id: " + postId + " was successfully deleted";
    }

    public List getPostList() {

        List postList = List.of();

        try {
            postList = webClientBuilder.build().get()
                    .uri("http://store-service/api/store/files")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to reach posts.");
            e.printStackTrace();
        }

        log.info("List of posts was successfully received.");
        return postList;
    }


}

