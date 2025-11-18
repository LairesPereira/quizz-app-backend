package com.quizz.app.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlternativesWithAiDTO {
    private String content;
    private boolean isCorrect;
}
