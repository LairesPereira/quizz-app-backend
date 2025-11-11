package com.quizz.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDTO {
    @Size(min = 1, max = 100000, message = "A resposta deve ter entre 1 e 100000 caracteres")
    private String content;

    @JsonProperty("isCorrect")
    private boolean isCorrect;
}
