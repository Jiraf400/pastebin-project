package com.jirafik.hash.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @Column
    private String id;
    private String title;
    private String content;
    private String img;
    private List<String> tags;

}
