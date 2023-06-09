package com.jirafik.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.OutputStream;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutputResponse {

    private String postID;

    private byte[] content;
}
