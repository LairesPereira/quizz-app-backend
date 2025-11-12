package com.quizz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizzStartedInfoDTO {
    private String id;
    private String participantId;
    private String quizzSlug;
    private String title;
    private String description;
    private Boolean isMobileAllowed;
    private Boolean allowUserSeeResults;
    private double maxScore;
    private List<QuestionDTO> questions;
}

