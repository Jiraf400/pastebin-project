package com.jirafik.post.service;

import com.jirafik.post.entity.Post;
import com.jirafik.post.entity.PostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PostService {

    private final WebClient.Builder webClientBuilder;

    public Post upload(PostRequest request) {

        request.setId(String.valueOf(new Random().nextLong(100000, 999999L)));

        System.out.println("PostService PostRequest.class : \n" + request);

        var response = webClientBuilder.build().post()
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

//        webClientBuilder.build().post()
//                .uri("http://hash-service/api/hash/setLink",
//                        uriBuilder -> uriBuilder.queryParam("postRequest", request).build())
//                .retrieve()
//                .bodyToMono(Link.class)
//                .block();

    public String getPostLink(String postId) {

        return webClientBuilder.build().get()
                .uri("http://hash-service/api/hash/getLink",
                        uriBuilder -> uriBuilder.queryParam("postID", postId).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String deletePost(String postId) {

        return webClientBuilder.build().delete()
                .uri("http://store-service/api/store/delete",
                        uriBuilder -> uriBuilder.queryParam("postID", postId).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public List getFileList() {
        return webClientBuilder.build().get()
                .uri("http://store-service/api/store/files")
                .retrieve()
                .bodyToMono(List.class)
                .block();
    }

}

