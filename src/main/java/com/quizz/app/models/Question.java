package com.quizz.app.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @Size(min = 3, max = 100000, message = "Message should have at least 3 caracters.")
    private String content;

    @NotBlank
    @Size(min = 3, max = 100000, message = "Answer should have at least 3 caracters.")
    private String answer = "";

    @OneToMany(mappedBy = "question", cascade = {CascadeType.MERGE, CascadeType.ALL, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JsonManagedReference
    @NotEmpty(message = "A questão deve conter pelo menos uma resposta.")
    private List<@Valid Answer> answers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quizz_id")
    @JsonBackReference
    private Quizz quizz;
}
