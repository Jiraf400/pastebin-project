package com.jirafik.store.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@RedisHash("StoredPost")
public class StoredPost implements Serializable {

    @Id
    private String id;

    private String fileName;

    private String dateOfCreation;

    private String wroteBy;

    private String postTitle;
}
