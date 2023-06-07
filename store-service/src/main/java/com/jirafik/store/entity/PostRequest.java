package com.jirafik.store.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
import java.util.Random;

@ToString
@Builder
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
