package com.inference.whatsappintegration.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping(value = "/")
    public String hello() {
        return "Hello World from Tomcat";
    }
}
