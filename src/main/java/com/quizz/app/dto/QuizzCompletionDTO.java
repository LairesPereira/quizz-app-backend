package com.quizz.app.dto;

import com.quizz.app.dto.ParticipantAnswerDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizzCompletionDTO {
    @NotBlank
    private String participantId;
    @NotBlank
    private String quizzSlug;
    @NotNull
    private List<ParticipantAnswerDTO> answers;
}