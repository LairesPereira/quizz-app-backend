package com.quizz.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantBasicInfoDTO {
    private String email;
    private String name;
    private String quizzSlug;
}
