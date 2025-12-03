package com.quizz.app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class HelloController {

    @GetMapping("")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello! Nothing here... :D");
    }
}
