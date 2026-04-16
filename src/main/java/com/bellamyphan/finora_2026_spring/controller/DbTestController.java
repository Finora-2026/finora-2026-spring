package com.bellamyphan.finora_2026_spring.controller;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-db")
@RequiredArgsConstructor
public class DbTestController {

    private final EntityManager entityManager;

    @GetMapping
    public String testConnection() {
        String version = (String) entityManager
                .createNativeQuery("SELECT version()")
                .getSingleResult();

        return "DB Connected (JPA): " + version;
    }
}
