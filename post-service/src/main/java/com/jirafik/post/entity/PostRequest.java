package com.jirafik.post.entity;

import lombok.*;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    private String id;
    private String title;
    private String content;
    private String img;
    private List<String> tags;

}
