package com.quizz.app.services;

import com.quizz.app.dto.*;
import com.quizz.app.dto.result.ParticipantResultDTO;
import com.quizz.app.dto.result.QuestionAndAnswerDTO;
import com.quizz.app.errors.ForbiddenException;
import com.quizz.app.errors.ResourceNotFound;
import com.quizz.app.models.*;
import com.quizz.app.repositorie.ParticipantRepository;
import com.quizz.app.repositorie.QuestionAndAnswerRepository;
import com.quizz.app.repositorie.QuizzRepository;
import com.quizz.app.repositorie.QuizzResultRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizzServices {

    @Autowired
    QuizzRepository quizzRepository;
    @Autowired
    QuizzResultRepository quizzResultRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    QuestionAndAnswerRepository questionAndAnswerRepository;

    @Transactional
    public Quizz save(CreateQuizzDTO createQuizzDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Quizz quizz = Quizz.builder()
                .user(user)
                .title(createQuizzDTO.getTitle())
                .description(createQuizzDTO.getDescription())
                .maxScore(createQuizzDTO.getMaxScore())
                .isMobileAllowed(createQuizzDTO.getIsMobileAllowed())
                .allowUserSeeResults(createQuizzDTO.getAllowUserSeeResults())
                .allowDuplicateEmailOnQuizz(createQuizzDTO.getAllowDuplicateEmailOnQuizz())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(true)
                .slug(new SecureRandom()
                        .ints(17, 0, 36)
                        .mapToObj(i -> Integer.toString(i, 36))
                        .collect(Collectors.joining())
                        .toUpperCase()
                )
                .build();

        // Mapeia as perguntas e respostas
        List<Question> questions = createQuizzDTO.getQuestions().stream().map(questionDTO -> {

            Question question = Question.builder()
                    .content(questionDTO.getContent())
                    .quizz(quizz)
                    .build();

            List<Answer> answers = questionDTO.getAnswers().stream().map(answerDTO ->
                    Answer.builder()
                            .answer(answerDTO.getContent())
                            .isCorrect(answerDTO.isCorrect())
                            .question(question)
                            .build()
            ).collect(Collectors.toList());

            validateAnswer(answers);
            question.setAnswers(answers);

            return question;
        }).collect(Collectors.toList());

        // link correct answer content to question
        questions.forEach(q ->
                q.getAnswers()
                        .stream()
                        .filter(Answer::isCorrect)
                        .findFirst()
                        .ifPresent(a -> q.setAnswer(a.getAnswer()))
        );
        quizz.setQuestions(questions);

        return quizzRepository.save(quizz);
    }

    private void validateAnswer(List<Answer> answers) {
        long correct = answers.stream()
                .filter(Answer::isCorrect)
                .count();

        if (correct != 1) {
            throw new IllegalArgumentException("A questão deve ter apenas uma resposta correta.");
        }
    }

    public boolean toggleStatus(String slug) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Quizz quizz = quizzRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFound("Quizz not found"));

        if (!quizz.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not allowed to toggle this status");
        }
        quizz.setStatus(!quizz.isStatus());
        quizzRepository.save(quizz);
        return true;
    }

    public List<QuizzBasicInfoDTO> findAll() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Quizz> quizzes = quizzRepository.findAllByUserId(user.getId());
        return quizzes.stream().map(quizz -> QuizzBasicInfoDTO.builder()
                .title(quizz.getTitle())
                .description(quizz.getDescription())
                .status(quizz.isStatus())
                .maxScore(quizz.getMaxScore())
                .slug(quizz.getSlug())
                .createdAt(quizz.getCreatedAt())
                .participants(countQuizzParticipants(quizz.getId()))
                .build()).collect(Collectors.toList());
    }

    private long countQuizzParticipants(String quizz_id) {
        return quizzRepository.countParticipantsByQuizzId(quizz_id);
    }

    public List<ParticipantQuizzInfo> getParticipantsBasicInfoByQuizzSlug(String slug) {
        Quizz quizz = quizzRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFound("Quizz not found"));

        List<Participant> quizzParticipants = quizz.getParticipants();
        List<ParticipantQuizzInfo> participantQuizzInfoList = quizz.getParticipants().stream()
                .map(participant -> ParticipantQuizzInfo.builder()
                        .id(participant.getId())
                        .name(participant.getName())
                        .email(participant.getEmail())
                        .score(getParticipantScoreByQuizzSlug(quizz.getId(), participant.getId()))
                        .maxScore(quizz.getMaxScore())
                        .build())
                .collect(Collectors.toList());
        System.err.println(quizzParticipants.size());
        return participantQuizzInfoList;
    }

    public Quizz findBySlug(String slug) {
        return quizzRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFound("Quizz not found"));
    }

    public double getParticipantScoreByQuizzSlug(String quizzId, String participantId) {
        QuizzResult quizzResult = quizzResultRepository.findByParticipantIdAndQuizzId(participantId, quizzId)
                        .orElseThrow(() -> new ResourceNotFound("Não foi possível encontrar o score para o participante: " + participantId));

        if (quizzResult.getScore() == null) {
            throw new ResourceNotFound("Não foi possível encontrar o score para o participante: " + participantId);
        }

        return quizzResult.getScore();
    }

    public double getMaxScoreBySlug(String slug) {
        Quizz quizz = quizzRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFound("Quizz not found"));
        return quizz.getMaxScore();
    }

    public ParticipantResultDTO getParticipantResultByQuizzSlugAndParticipantId(String slug, String participantId) {
        Quizz quizz = quizzRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFound("Quizz not found"));

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFound("Participant not found"));

        QuizzResult quizzResult = quizzResultRepository.findByParticipantIdAndQuizzId(participantId, quizz.getId())
                .orElseThrow(() -> new ResourceNotFound("Result not found"));


        List<QuestionAndAnswer> qaList = questionAndAnswerRepository.findByQuizzResultId(quizzResult.getId());

        List<QuestionAndAnswerDTO> questionDTOs = qaList.stream()
                .map(qa -> QuestionAndAnswerDTO.builder()
                       .id(qa.getId())
                       .questionText(qa.getOriginalQuestion())
                       .correctAnswer(qa.getCorrectAnswer())
                       .participantAnswer(qa.getAnswer())
                       .build())
                .collect(Collectors.toList());

        return ParticipantResultDTO.builder()
                .name(participant.getName())
                .email(participant.getEmail())
                .maxScore(quizz.getMaxScore())
                .score(quizzResult.getScore())
                .questions(questionDTOs)
                .build();
    }

//    @Transactional
//    public void deleteBySlug(String slug) {
//        Quizz quizz = quizzRepository.findBySlug(slug)
//                .orElseThrow(() -> new ResourceNotFound("Quizz not found"));
//
//        quizz.getParticipants().clear();
//        quizzResultRepository.deleteAllByQuizzId(quizz.getId());
//        quizzRepository.delete(quizz);
//    }

    public StatisctsResopnseDTO getStatistics() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getQuizzList().isEmpty()) {
            throw new ResourceNotFound("Não foi possível encontrar os quizzes do usuário!");
        }

        long totalQuizzes = user.getQuizzList().size();
        long totalParticipants = 0;
        double sumScores = 0;

        List<QuizzResult> quizzResults = new ArrayList<>();
        for (Quizz quizz : user.getQuizzList()) {
            totalParticipants += quizz.getParticipants().size();
            for (Participant participant : quizz.getParticipants()) {
                 quizzResults.addAll(quizzResultRepository.findAllByParticipantId(participant.getId()));
            }
        }
        for (QuizzResult quizzResult : quizzResults) {
            sumScores += quizzResult.getScore();
        }

        return StatisctsResopnseDTO.builder()
                .totalQuizzes(totalQuizzes)
                .totalParticipants(totalParticipants)
                .meanScore(sumScores / totalParticipants)
                .build();
    }
}
