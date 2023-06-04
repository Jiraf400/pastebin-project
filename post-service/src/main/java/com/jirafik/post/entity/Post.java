package com.jirafik.post.entity;


import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Post {

    private String id;

    private String title;

    private Date dateOfCreation;

    private String content;

    private String wroteBy;

    private String img;

    private List<String> tags;

}
