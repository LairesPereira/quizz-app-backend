package com.quizz.app.repositorie;

import com.quizz.app.models.QuizzResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizzResultRepository extends JpaRepository<QuizzResult, String> {
    QuizzResult findByParticipantIdAndQuizzId(String participantId, String quizzId);

    void deleteAllByQuizzId(String id);
}
