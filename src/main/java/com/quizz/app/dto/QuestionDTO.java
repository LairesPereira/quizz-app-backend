package com.quizz.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {
    private String id = UUID.randomUUID().toString();

    @Size(min = 1, max = 1000, message = "A questão deve ter entre 1 e 1000 caracteres")
    private String content;

    @NotNull(message = "As respostas não devem ser nulas")
    @Size(min = 1, message = "A questão deve conter pelo menos uma resposta")
    @Valid
    private List<AnswerDTO> answers;
}
