package com.quizz.app.dto;

import lombok.Data;

@Data
public class ParticipantBasicInfoDTO {
    private String email;
    private String name;
    private String quizzSlug;
}
