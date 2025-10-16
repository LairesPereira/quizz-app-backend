package com.quizz.app.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionAndAnswerDTO {
    private String id;
    private String questionText;
    private String correctAnswer;
    private String participantAnswer;
    private String answer;
}
