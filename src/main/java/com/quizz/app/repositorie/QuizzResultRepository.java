package com.quizz.app.repositorie;

import com.quizz.app.models.QuizzResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizzResultRepository extends JpaRepository<QuizzResult, String> {
    Optional<QuizzResult> findByParticipantIdAndQuizzId(String participantId, String quizzId);

    void deleteAllByQuizzId(String id);

    List<QuizzResult> findAllByParticipantId(String participantId);
}
