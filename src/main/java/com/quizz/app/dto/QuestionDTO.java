package com.quizz.app.dto;

import com.quizz.app.models.Answer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {
    private String id = UUID.randomUUID().toString();
    @NotBlank
    @Size(min = 1, max = 1000, message = "A resposta deve ter entre 1 e 1000 caracteres")
    private String content;

    @NotBlank
    private List<AnswerDTO> answers;
}
