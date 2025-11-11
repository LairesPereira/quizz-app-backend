package com.quizz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisctsResopnseDTO {
    private Long totalQuizzes;
    private Long totalParticipants;
    private Long mean;
    private Long accuracyRate;
}
