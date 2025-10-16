package com.quizz.app.repositorie;

import com.quizz.app.models.QuestionAndAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionAndAnswerRepository extends JpaRepository<QuestionAndAnswer, String> {
    List<QuestionAndAnswer> findByQuizzResultId(String id);
}
