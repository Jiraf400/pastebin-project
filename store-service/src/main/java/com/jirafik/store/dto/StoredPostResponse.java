package com.jirafik.store.dto;

import jakarta.persistence.Id;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoredPostResponse {

    @Id
    private String id;

    private String postTitle;

}

