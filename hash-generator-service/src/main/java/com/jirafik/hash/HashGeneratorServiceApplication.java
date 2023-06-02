package com.jirafik.hash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HashGeneratorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HashGeneratorServiceApplication.class, args);
    }

}
