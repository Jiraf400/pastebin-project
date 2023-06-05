package com.jirafik.store.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;

@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @JsonProperty
    private String id;
    @JsonProperty
    private String title;
    @JsonProperty
    private String content;
    @JsonProperty
    private String img;
    @JsonProperty
    private List<String> tags;

}
