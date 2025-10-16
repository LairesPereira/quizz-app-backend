package com.quizz.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateQuizzDTO {
    @NotBlank
    @Size(min = 3, max = 5000, message = "O titulo deve ter entre 3 e 1000 caracteres")
    private String title;
    @NotBlank
    @Size(min = 3, max = 50000, message = "O conteudo deve ter entre 3 e 10000 caracteres")
    private String description;
    @NotNull
    private double maxScore;
    @NotNull
    private List<QuestionDTO> questions;
}
