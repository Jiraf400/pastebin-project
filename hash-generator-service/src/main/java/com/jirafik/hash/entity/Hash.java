package com.jirafik.hash.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Hash {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    private String hash;

    private String postId;

    private String url;
}

