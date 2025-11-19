package com.quizz.app.controllers;

import com.quizz.app.dto.AlternativesWithAiDTO;
import com.quizz.app.services.GeminiApi;
import com.quizz.app.services.GeminiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionsController {

    @Autowired
    GeminiServices geminiServices;

    @GetMapping("/generate-alternatives")
    public List<AlternativesWithAiDTO> generateAlternativesWithAi (@RequestParam String questionText) {
        return geminiServices.generateAlternativesWithAi(questionText);
    }

    @GetMapping("/generate-single-alternative")
    public AlternativesWithAiDTO generateSingleAlternativeWithAi (@RequestParam String questionText) {
        return geminiServices.generateSingleAlternativesWithAi(questionText);
    }
}
