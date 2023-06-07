package com.jirafik.store.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class StoredPost {

    @Id
    private String id;

    private String postTitle;

    private String fileId;
}
