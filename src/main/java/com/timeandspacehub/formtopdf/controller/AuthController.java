package com.timeandspacehub.formtopdf.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    @GetMapping("/auth-check")
    public ResponseEntity<String> authCheck() {
        // If we reach here, user is authenticated by Spring Security
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}