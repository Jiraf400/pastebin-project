package com.jirafik.store;

import com.jirafik.store.service.DropBoxAuthenticationManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
public class StoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreServiceApplication.class, args);

        //Dropbox authorization flow only supports short-lived tokens that can be refreshed using refresh token
        //So I didn't find anything better than set system timer for automatically sending request to api.

        ScheduledExecutorService executorService;
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(DropBoxAuthenticationManager::authorize, 0, 3, TimeUnit.HOURS);

    }

}
