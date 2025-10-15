package com.quizz.app.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.quizz.app.models.Participant;
import com.quizz.app.models.Question;
import com.quizz.app.models.Quizz;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "quizz_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizzResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "quizz_id")
    private Quizz quizz;

    @NotNull
    private Double score;

    @OneToMany(mappedBy = "quizzResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<QuestionAndAnswer> questionsAndAnswers;

}
