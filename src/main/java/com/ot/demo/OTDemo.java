package com.ot.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class OTDemo {
    public static void main(String[] args) {
        SpringApplication.run(OTDemo.class, args);
    }

    @RequestMapping(value = "/test")
    public String hello() {
        return "Hello World";
    }
}
