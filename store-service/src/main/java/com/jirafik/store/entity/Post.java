package com.jirafik.store.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@RedisHash("Post")
public class Post implements Serializable {

    @JsonProperty
    private String id;
    @JsonProperty
    private String title;
    @JsonProperty
    private String dateOfCreation;
    @JsonProperty
    private String content;
    @JsonProperty
    private String wroteBy;
    @JsonProperty
    private String img;
    @JsonProperty
    private List<String> tags;

}

