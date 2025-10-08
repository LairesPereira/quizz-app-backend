package com.quizz.app.dto;

import com.quizz.app.models.Answer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {
    @NotBlank
    @Size(min = 1, max = 1000, message = "A resposta deve ter entre 1 e 1000 caracteres")
    private String content;

    @NotBlank
    @Size(min = 1, max = 1000, message = "answer should have at least one character and max 1000.")
    private String answer;

    @NotBlank
    private List<AnswerDTO> answers;
}
