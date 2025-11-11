package com.quizz.app.repositorie;

import com.quizz.app.dto.ScoreInfoDTO;
import com.quizz.app.models.Quizz;
import com.quizz.app.models.QuizzResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizzRepository extends JpaRepository<Quizz, String> {
    Quizz save(Quizz quizz);
    Quizz findBySlug(String slug);
    List<Quizz> findAllByUserId(String id);

    @Query(value = "SELECT COUNT(*) FROM participant_quizz WHERE quizz_id = :quizz_id", nativeQuery = true)
    long countParticipantsByQuizzId(@Param("quizz_id") String quizz_id);

    long countByUserId(String id);
}
