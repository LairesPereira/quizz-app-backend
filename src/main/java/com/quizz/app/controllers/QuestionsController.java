package com.quizz.app.controllers;

import com.quizz.app.dto.AlternativesWithAiDTO;
import com.quizz.app.services.GeminiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionsController {

    private final Logger log = LoggerFactory.getLogger(QuestionsController.class);

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
