package com.quizz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuizzBasicInfoDTO {
    private String title;
    private String description;
    private boolean status;
    private double maxScore;
    private String slug;
    private LocalDateTime createdAt;
    private long participants;
}
