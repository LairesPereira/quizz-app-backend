package com.quizz.app.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @Size(min = 3, max = 100, message = "Message should have at least 3 caracters.")
    private String content;

    @NotBlank
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizz_id")
    @JsonBackReference
    private Quizz quizz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch =  FetchType.EAGER)
    private List<Answer> answers;

}
