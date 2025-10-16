package com.quizz.app.dto.result;

import com.quizz.app.models.QuestionAndAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantResultDTO {
    private String name;
    private String email;
    private double score;
    private double maxScore;
    private List<QuestionAndAnswerDTO> questions;
}
