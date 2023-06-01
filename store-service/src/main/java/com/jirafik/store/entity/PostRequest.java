package com.jirafik.store.entity;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequest {

    private String title;

    private String content;

    private String img;

    private List<Tag> tags;
}
