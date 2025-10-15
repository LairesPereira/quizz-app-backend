package com.quizz.app.models;

import lombok.Data;

@Data
public class ParticipantBasicInfoDTO {
    private String email;
    private String name;
    private String quizzSlug;
}
