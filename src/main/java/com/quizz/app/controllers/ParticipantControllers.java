package com.quizz.app.controllers;

import com.quizz.app.dto.QuizzCompletionDTO;
import com.quizz.app.dto.QuizzStartedInfoDTO;
import com.quizz.app.dto.ParticipantBasicInfoDTO;
import com.quizz.app.services.ParticipantServices;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/participant")
public class ParticipantControllers {

    private static final Logger log = LoggerFactory.getLogger(ParticipantControllers.class);

    @Autowired
    ParticipantServices participantServices;

    @PostMapping("/quizz")
    public ResponseEntity<?> startQuizz(@RequestBody @Valid ParticipantBasicInfoDTO participantBasicInfoDTO) {
        log.info("User {} solicitou para iniciar o quizz {}", participantBasicInfoDTO.getEmail(), participantBasicInfoDTO.getQuizzSlug());
        var response = participantServices.startQuizz(participantBasicInfoDTO);
        log.info("Quizz {} iniciado para o participante {}", participantBasicInfoDTO.getQuizzSlug(), participantBasicInfoDTO.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quizz/submit")
    public ResponseEntity<?> completeQuizz(@RequestBody @Valid QuizzCompletionDTO quizCompletionDTO) {
        participantServices.saveAnswers(quizCompletionDTO);
        return ResponseEntity.ok(Map.of("message", "Quiz finalizado com sucesso!"));
    }

//    @Transactional
//    @DeleteMapping("/quizz/remove{id}")
//    public ResponseEntity<?> removeParticipant(@RequestParam String id) {
//        if (participantServices.removeParticipantsById(id) != null) {
//            return ResponseEntity.ok().build();
//        }
//        return  ResponseEntity.badRequest().build();
//    }
}
