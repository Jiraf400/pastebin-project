package com.jirafik.post.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Link {

    private String id;

    private boolean isExpired;

    private String link;

    private PostRequest postRequest;
}

