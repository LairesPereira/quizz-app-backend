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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "quizz")
public class Quizz {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @Size(max = 100000)
    private String title;

    @Size(max = 100000)
    private String description;

    @NotNull
    private boolean status;

    @NotNull
    private double maxScore;

    @NotBlank
    @Column(unique = true)
    private String slug;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "quizz", cascade = {CascadeType.MERGE, CascadeType.ALL, CascadeType.REMOVE}, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<Question> questions;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "participant_quizz",
            joinColumns = @JoinColumn(name = "quizz_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @JsonManagedReference
    private List<Participant> participants;
}
