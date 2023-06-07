package com.jirafik.hash.repository;

import com.jirafik.hash.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashRepository extends JpaRepository<Link, Integer> {

}
