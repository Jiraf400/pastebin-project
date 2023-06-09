package com.jirafik.store.dto;

import lombok.*;

import java.io.OutputStream;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutputResponse {

    private String postID;

    private byte[] content;
}
