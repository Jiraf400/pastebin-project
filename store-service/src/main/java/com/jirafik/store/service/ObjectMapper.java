package com.jirafik.store.service;

import com.jirafik.store.dto.PostRequest;
import com.jirafik.store.dto.StoredPostResponse;
import com.jirafik.store.entity.Post;
import com.jirafik.store.entity.StoredPost;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class ObjectMapper {

    public Post mapToPost(PostRequest request) {
        return Post.builder()
                .id(request.getId())
                .title(request.getTitle())
                .img(request.getImg())
                .content(request.getContent())
                .tags(request.getTags())
                .dateOfCreation(Date.from(Instant.now()))
                .wroteBy(request.getWroteBy())
                .build();
    }

    public StoredPostResponse mapToStoredPostResponse(StoredPost storedPost) {
        return StoredPostResponse.builder()
                .id(storedPost.getId())
                .postTitle(storedPost.getPostTitle())
                .build();
    }

    public StoredPost mapToStoredPost(Post post) {

        return StoredPost.builder()
                .id(post.getId())
                .dateOfCreation(post.getDateOfCreation())
                .wroteBy(post.getWroteBy())
                .postTitle(post.getTitle())
                .fileName(post.getId() + ".json")
                .build();

    }
}
