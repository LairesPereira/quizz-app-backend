package com.quizz.app.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private Double score = 0.;

    @OneToMany(mappedBy = "quizzResult", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JsonManagedReference
    private List<QuestionAndAnswer> questionsAndAnswers;

}
