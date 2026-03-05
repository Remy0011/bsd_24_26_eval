package edu.ban7.demo_spring_bsd_24_26.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "<h1>Ca marche</h1>, mais y'a rien a voir ici";
    }

    @GetMapping("/hello")
    public String direBonjour() {
        return "Hello World";
    }

}
