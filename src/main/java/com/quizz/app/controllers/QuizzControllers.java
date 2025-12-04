package com.quizz.app.controllers;

import com.quizz.app.dto.CreateQuizzDTO;
import com.quizz.app.dto.ParticipantQuizzInfo;
import com.quizz.app.dto.QuizzBasicInfoDTO;
import com.quizz.app.dto.result.ParticipantResultDTO;
import com.quizz.app.services.QuizzServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quizz")
@SecurityRequirement(name = "bearerAuth")
public class QuizzControllers {

    private final static Logger log = LoggerFactory.getLogger(QuizzControllers.class);

    @Autowired
    QuizzServices quizzServices;

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createQuizz(@Valid @RequestBody CreateQuizzDTO createQuizzDTO) {
        if (quizzServices.save(createQuizzDTO) != null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/toggle-status")
    public ResponseEntity<?> toggleQuizzStatus(@RequestParam String slug) {
        if (quizzServices.toggleStatus(slug)) {
            return ResponseEntity.ok().build();
        }
        return  ResponseEntity.badRequest().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<QuizzBasicInfoDTO>> getAllQuizzes() {
        List<QuizzBasicInfoDTO> quizzes = quizzServices.findAll();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/participants/info{slug}")
    public ResponseEntity<List<ParticipantQuizzInfo>> getParticipantsByQuizzSlug(@RequestParam String slug) {
        List<ParticipantQuizzInfo> participants = quizzServices.getParticipantsBasicInfoByQuizzSlug(slug);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("participant/result{slug}{participantId}")
    public ResponseEntity<ParticipantResultDTO> getParticipantResult(
            @RequestParam("slug") String slug,
            @RequestParam("participantId") String participantId) {

        ParticipantResultDTO participantResultDTO = quizzServices.getParticipantResultByQuizzSlugAndParticipantId(slug, participantId);

        return ResponseEntity.ok(participantResultDTO);
    }

    @GetMapping("/info/max-score{slug}")
    public ResponseEntity<?> getQuizzMaxScore(@RequestParam String slug) {
        return ResponseEntity.ok(Map.of("maxScore", quizzServices.getMaxScoreBySlug(slug)));
    }
}
