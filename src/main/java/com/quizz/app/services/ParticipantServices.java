package com.quizz.app.services;

import com.quizz.app.dto.*;
import com.quizz.app.errors.ForbiddenException;
import com.quizz.app.errors.ResourceNotFound;
import com.quizz.app.models.*;
import com.quizz.app.repositorie.ParticipantRepository;
import com.quizz.app.repositorie.QuizzRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParticipantServices {

    private static final Logger log = LoggerFactory.getLogger(ParticipantServices.class);

    @Autowired
    QuizzRepository quizzRepository;
    @Autowired
    ParticipantRepository participantRepository;

    public QuizzStartedInfoDTO startQuizz(ParticipantBasicInfoDTO participantBasicInfoDTO) {
        Quizz quizz = quizzRepository.findBySlug(participantBasicInfoDTO.getQuizzSlug())
                .orElseThrow(() -> {
                    log.warn("[START_QUIZZ] Quizz {} não encontrado para o usuário {}", participantBasicInfoDTO.getQuizzSlug(), participantBasicInfoDTO.getEmail());
                    return new ResourceNotFound("Quizz '" + participantBasicInfoDTO.getQuizzSlug() + "' não encontrado");
                });

        if (!quizz.isStatus()) {
            log.warn("[START_QUIZZ] Quizz {} não está disponível para {}", participantBasicInfoDTO.getQuizzSlug(), participantBasicInfoDTO.getEmail());
            throw new ForbiddenException("O Quizz não está disponível");
        }

        if (!quizz.getAllowDuplicateEmailOnQuizz()) {
            if (quizz.getParticipants().stream()
                    .anyMatch(p -> p.getEmail().equals(participantBasicInfoDTO.getEmail()))) {

                log.warn("[START_QUIZZ] Participante já cadastrado no quizz {} para o usuário {}",
                        participantBasicInfoDTO.getQuizzSlug(),
                        participantBasicInfoDTO.getEmail()
                );

                throw new ForbiddenException("Participante já cadastrado no quizz");
            }
        }

        log.info("[START_QUIZZ] Cadastrando participante {} no quizz {}", participantBasicInfoDTO.getEmail(), participantBasicInfoDTO.getQuizzSlug());
        Participant participant = participantRepository.save(Participant.builder()
                .name(participantBasicInfoDTO.getName())
                .email(participantBasicInfoDTO.getEmail())
                .status(true)
                .quizzes(new ArrayList<>())
                .build()
        );

        quizz.getParticipants().add(participant);
        quizzRepository.save(quizz);

        List<QuestionDTO> questionDTOs = mapQuestionToDTO(quizz);

        return QuizzStartedInfoDTO.builder()
                .id(quizz.getId())
                .participantId(participant.getId())
                .title(quizz.getTitle())
                .description(quizz.getDescription())
                .isMobileAllowed(quizz.getIsMobileAllowed())
                .allowUserSeeResults(quizz.getAllowUserSeeResults())
                .quizzSlug(quizz.getSlug())
                .maxScore(quizz.getMaxScore())
                .questions(questionDTOs)
                .build();
    }

    private static List<QuestionDTO> mapQuestionToDTO(Quizz quizz) {
        return quizz.getQuestions().stream()
                .map(q -> QuestionDTO.builder()
                        .id(q.getId())
                        .content(q.getContent())
                        .answers(q.getAnswers().stream()
                                .map(a -> AnswerDTO.builder()
                                        .content(a.getAnswer())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    public void saveAnswers(QuizzCompletionDTO quizCompletionDTO) {
        log.info("[SAVE_ANSWERS] Salvando respostas do quizz {} para o participante {}", quizCompletionDTO.getQuizzSlug(), quizCompletionDTO.getParticipantId());

        Quizz quizz = quizzRepository.findBySlug(quizCompletionDTO.getQuizzSlug())
                .orElseThrow(() -> {
                    log.warn("Quizz {} não encontrado ao tentar finalizar", quizCompletionDTO.getQuizzSlug());
                    return new ResourceNotFound("Não foi possível encontrar o quizz");
                });

        Participant participant = participantRepository.findById(quizCompletionDTO.getParticipantId())
                .orElseThrow(() -> {
                        log.warn("[SAVE_ANSWERS] Participante {} não encontrado ao tentar finalizar", quizCompletionDTO.getParticipantId());
                        return new ResourceNotFound("Não foi possível encontrar o participante");
                    }
                );

        if (!quizz.isStatus()) {
            log.warn("[SAVE_ANSWERS] Quizz {} não está disponível ao tentar finalizar", quizCompletionDTO.getQuizzSlug());
            throw new ForbiddenException("O Quizz não está disponível no momento");
        }

        if (!quizz.getParticipants().contains(participant)) {
            log.warn("[SAVE_ANSWERS] Participante {} não encontrado no quizz {} ao tentar finalizar", quizCompletionDTO.getParticipantId(), quizCompletionDTO.getQuizzSlug());
            throw new ResourceNotFound("Participant not found in this quizz");
        }

        double score = calculateScore(quizCompletionDTO.getAnswers(), quizz.getQuestions(), quizz.getMaxScore());

        QuizzResult quizzResult = QuizzResult.builder()
                .participant(participant)
                .quizz(quizz)
                .score(score)
                .build();

        List<QuestionAndAnswer> questionAndAnswerList = new ArrayList<>();

        populateQuestionAndAnswers(quizCompletionDTO, quizz, quizzResult, questionAndAnswerList);

        quizzResult.setQuestionsAndAnswers(questionAndAnswerList);
    }

    private static void populateQuestionAndAnswers(QuizzCompletionDTO quizCompletionDTO, Quizz quizz, QuizzResult quizzResult, List<QuestionAndAnswer> questionAndAnswerList) {
        for (ParticipantAnswerDTO participantAnswer : quizCompletionDTO.getAnswers()) {
            Question question = quizz.getQuestions().stream()
                    .filter(q -> q.getId().equals(participantAnswer.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFound("Question not found"));

            QuestionAndAnswer qa = QuestionAndAnswer.builder()
                    .originalQuestion(question.getContent())
                    .correctAnswer(question.getAnswer())
                    .answer(participantAnswer.getAnswerText())
                    .quizzResult(quizzResult)
                    .build();

            questionAndAnswerList.add(qa);
        }
    }

    double calculateScore(@NotNull List<ParticipantAnswerDTO> answers, List<Question> questions, double maxScore) {
        double score = 0;
        for (ParticipantAnswerDTO answer : answers) {
            for (Question question : questions) {
                if (question.getId().equals(answer.getQuestionId())) {
                    if (question.getAnswer().equals(answer.getAnswerText())) {
                        score += maxScore / questions.size();
                    }
                }
            }
        }
        return score;
    }
}
