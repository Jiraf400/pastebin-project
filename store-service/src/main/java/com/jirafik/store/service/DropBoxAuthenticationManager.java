package com.jirafik.store.service;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Service
public class DropBoxAuthenticationManager {



    //connect  to dropbox authorization server
    public static String authorize() {

        log.info("LOG: Authorize method was called");

        String curl = "curl -X POST https://api.dropbox.com/oauth2/token -d grant_type=refresh_token -d refresh_token=" +
                REFRESH_TOKEN + " -u " + APP_KEY + ":" + APP_SECRET;

        ProcessBuilder processBuilder = new ProcessBuilder(curl.split(" "));

        Process process = null;

        try {
            process = processBuilder.start();
        }catch (Exception e) {
            e.printStackTrace();
        }

        InputStream response = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response));

        var lines = reader.lines().toList();
        String line = lines.get(0);

        String[] parts = line.split("\"");

        return parts[3];
    }

    private final DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();

    public DbxClientV2 getCurrentUser() {
        return new DbxClientV2(config, authorize());
    }
}
