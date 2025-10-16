package com.quizz.app.controllers;

import com.quizz.app.dto.QuizzCompletionDTO;
import com.quizz.app.dto.QuizzStartedInfoDTO;
import com.quizz.app.dto.ParticipantBasicInfoDTO;
import com.quizz.app.services.ParticipantServices;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/participant")
public class ParticipantControllers {
    @Autowired
    ParticipantServices participantServices;

    @PostMapping("/quizz")
    public ResponseEntity<?> startQuizz(@RequestBody @Valid ParticipantBasicInfoDTO participantBasicInfoDTO) {
        QuizzStartedInfoDTO quizzStartedInfoDTO = participantServices.startQuizz(participantBasicInfoDTO);
        if (quizzStartedInfoDTO != null) {
            return ResponseEntity.ok(quizzStartedInfoDTO);
        }
        return  ResponseEntity.badRequest().build();
    }

    @PostMapping("/quizz/submit")
    public ResponseEntity<?> completeQuizz(@RequestBody @Valid QuizzCompletionDTO quizCompletionDTO) {
        participantServices.saveAnswers(quizCompletionDTO);
        return ResponseEntity.ok(Map.of("message", "Quiz finalizado com sucesso!"));
    }

    @Transactional
    @DeleteMapping("/quizz/remove{id}")
    public ResponseEntity<?> removeParticipant(@RequestParam String id) {
        if (participantServices.removeParticipantsById(id) != null) {
            return ResponseEntity.ok().build();
        }
        return  ResponseEntity.badRequest().build();
    }
}
