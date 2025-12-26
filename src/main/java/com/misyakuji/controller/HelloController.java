package com.misyakuji.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Security!";
    }

    @GetMapping("/home")
    public String home() {
        return "这个家啥都没有";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint.";
    }
}
