package com.codecluster.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "JWT Authentication Successful";
    }

    @GetMapping("/me")
    public String me(Authentication authentication) {
        return "Logged in User : " + authentication.getName();
    }
}