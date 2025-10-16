package com.quizz.app.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {
    private String content;
//    private List<ResultAnswerDto> answers;
    private int selectedAnswerIndex;
}
