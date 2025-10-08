package com.quizz.app.controllers;

import com.quizz.app.dto.CreateQuizzDTO;
import com.quizz.app.services.QuizzServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quizz")
@SecurityRequirement(name = "bearerAuth")
public class QuizzControllers {

    @Autowired
    QuizzServices quizzServices;

    @PostMapping("/create")
    public ResponseEntity<?> createQuizz(@Valid @RequestBody CreateQuizzDTO createQuizzDTO) {
        if (quizzServices.save(createQuizzDTO) != null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
