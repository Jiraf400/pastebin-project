package com.jirafik.store.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoredPostResponse {

    private String id;

    private String postTitle;

}

