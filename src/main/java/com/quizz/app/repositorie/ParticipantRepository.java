package com.quizz.app.repositorie;

import com.quizz.app.models.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, String> {
    Participant removeById(String id);
}
