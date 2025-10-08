package com.quizz.app.repositorie;

import com.quizz.app.models.Quizz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizzRepository extends JpaRepository<Quizz, String> {
    Quizz save(Quizz quizz);
    Quizz findBySlug(String slug);
}
