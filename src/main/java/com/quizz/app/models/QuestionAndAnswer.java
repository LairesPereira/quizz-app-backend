package com.quizz.app.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_answer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionAndAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String question;

    private String answer;

    @ManyToOne
    @JoinColumn(name = "quizz_result_id")
    @JsonBackReference
    private QuizzResult quizzResult;
}
