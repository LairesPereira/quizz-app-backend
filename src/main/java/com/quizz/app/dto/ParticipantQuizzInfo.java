package com.quizz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantQuizzInfo {
    private String id;
    private String name;
    private String email;
    private double score;
    private double maxScore;
}
