package com.quizz.app.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
    @Size(max = 100000)
    private String originalQuestion;
    @Size(max = 100000)
    private String correctAnswer;
    @Size(max = 100000)
    private String answer;

    @ManyToOne
    @JoinColumn(name = "quizz_result_id")
    @JsonBackReference
    private QuizzResult quizzResult;
}
