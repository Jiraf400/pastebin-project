package com.jirafik.hash.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Random;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @JsonProperty
    private String id = String.valueOf(new Random().nextLong(100000, 999999999999999999L));
    @JsonProperty
    private String title;
    @JsonProperty
    private String content;
    @JsonProperty
    private String img;
    @JsonProperty
    private List<String> tags;

}
