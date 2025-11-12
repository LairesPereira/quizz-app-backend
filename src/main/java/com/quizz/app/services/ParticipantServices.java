package com.quizz.app.services;

import com.quizz.app.dto.*;
import com.quizz.app.errors.ForbiddenException;
import com.quizz.app.errors.ResourceNotFound;
import com.quizz.app.models.*;
import com.quizz.app.repositorie.ParticipantRepository;
import com.quizz.app.repositorie.QuestionAndAnswerRepository;
import com.quizz.app.repositorie.QuizzRepository;
import com.quizz.app.repositorie.QuizzResultRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParticipantServices {
    @Autowired
    QuizzRepository quizzRepository;
    @Autowired
    ParticipantRepository participantRepository;;
    @Autowired
    QuizzResultRepository quizzResultRepository;
    @Autowired
    QuestionAndAnswerRepository questionAndAnswerRepository;

    public QuizzStartedInfoDTO startQuizz(ParticipantBasicInfoDTO participantBasicInfoDTO) {
        Quizz quizz = quizzRepository.findBySlug(participantBasicInfoDTO.getQuizzSlug());
        if (quizz == null || !quizz.isStatus()) {
            throw new ResourceNotFound("Quizz not found or not available");
        }
        if (!quizz.getAllowDuplicateEmailOnQuizz()) {
            quizz.getParticipants().stream()
                    .filter(p -> p.getEmail().equals(participantBasicInfoDTO.getEmail()))
                    .findFirst()
                    .ifPresent(p -> {
                        throw new ForbiddenException("Participant already exists");
                    });
        }

        Participant participant = participantRepository.save(Participant.builder()
                .name(participantBasicInfoDTO.getName())
                .email(participantBasicInfoDTO.getEmail())
                .status(true)
                .quizzes(new ArrayList<>())
                .build()
        );

        quizz.getParticipants().add(participant);
        quizzRepository.save(quizz);

        List<QuestionDTO> questionDTOs = quizz.getQuestions().stream()
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

        QuizzStartedInfoDTO finalQuizz = QuizzStartedInfoDTO.builder()
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
        return finalQuizz;
    }

    public void saveAnswers(QuizzCompletionDTO quizCompletionDTO) {
        Quizz quizz = quizzRepository.findBySlug(quizCompletionDTO.getQuizzSlug());
        Participant participant = participantRepository.findById(quizCompletionDTO.getParticipantId())
                .orElseThrow(() -> new ResourceNotFound("Participant not found"));

        if (quizz == null || !quizz.isStatus()) {
            throw new ResourceNotFound("Quizz not found or not available");
        }

        if (!quizz.getParticipants().contains(participant)) {
            throw new ResourceNotFound("Participant not found in this quizz");
        }

        double score = calculateScore(quizCompletionDTO.getAnswers(), quizz.getQuestions(), quizz.getMaxScore());

        QuizzResult quizzResult = QuizzResult.builder()
                .participant(participant)
                .quizz(quizz)
                .score(score)
                .build();

        List<QuestionAndAnswer> questionAndAnswerList = new ArrayList<>();

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

        quizzResult.setQuestionsAndAnswers(questionAndAnswerList);

        QuizzResult savedResult = quizzResultRepository.save(quizzResult);
    }


    private double calculateScore(@NotNull List<ParticipantAnswerDTO> answers, List<Question> questions, double maxScore) {
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

    public Participant removeParticipantsById(String id) {
        return participantRepository.removeById(id);
    }
}
