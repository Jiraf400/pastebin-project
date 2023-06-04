package com.jirafik.hash.controller;

import com.jirafik.hash.entity.PostRequest;
import com.jirafik.hash.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*              Задачи
Сервис должен связывать ссылки с json постами.
Пользователь отправляет запрос и получает ссылку на пост
Переходя по ссылке выполняется загрузка поста*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hash")
public class HashController {

    private final HashService service;

    @PostMapping("/postLink")
    public void postLink(PostRequest postRequest) {
        service.postLink(postRequest);
    }

    @GetMapping("/getLink")
    public String getLink(String id) {
        return service.getLink(id);
    }

}
