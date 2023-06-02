package com.jirafik.hash.entity;

import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoredPost {

    private String id;
    private Date expirationDate;
}
