package com.jirafik.store.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Post {

    @JsonProperty
    private String id;
    @JsonProperty
    private String title;
    @JsonProperty
    private Date dateOfCreation;
    @JsonProperty
    private String content;
    @JsonProperty
    private String wroteBy;
    @JsonProperty
    private String img;
    @JsonProperty
    private List<String> tags;

}
