package com.quizz.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.quizz.app.dto.QuestionDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateQuizzDTO {
    @Size(min = 3, max = 5000, message = "O titulo deve ter entre 3 e 1000 caracteres")
    private String title;

    @Size(min = 3, max = 50000, message = "A descrição deve ter entre 3 e 10000 caracteres")
    private String description;

    @NotNull(message = "O valor do score não deve ser nulo")
    private Double maxScore;

    @NotEmpty(message = "O quiz deve conter pelo menos uma questão.")
    private List<@Valid QuestionDTO> questions;
}
