package com.quizz.app.controllers;

import com.quizz.app.dto.CreateQuizzDTO;
import com.quizz.app.services.QuizzServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/status")
    public ResponseEntity<?> toggleQuizzStatus(@RequestParam String slug) {
        if (quizzServices.toggleStatus(slug)) {
            return ResponseEntity.ok().build();
        }
        return  ResponseEntity.badRequest().build();
    }
}
