package com.quizz.app.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "answer")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    private String answer;

    @NotNull
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "quizz_id")
    @JsonBackReference
    private Quizz quizz;

}
