package com.quizz.app.services;

import com.quizz.app.dto.CreateQuizzDTO;
import com.quizz.app.errors.ForbiddenException;
import com.quizz.app.errors.ResourceNotFound;
import com.quizz.app.models.Answer;
import com.quizz.app.models.Question;
import com.quizz.app.models.Quizz;
import com.quizz.app.models.User;
import com.quizz.app.repositorie.QuizzRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizzServices {

    @Autowired
    QuizzRepository quizzRepository;

    @Transactional
    public Quizz save(CreateQuizzDTO createQuizzDTO) {
        // Obtém o usuário autenticado
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Cria o Quizz base
        Quizz quizz = Quizz.builder()
                .user(user)
                .title(createQuizzDTO.getTitle())
                .description(createQuizzDTO.getDescription())
                .maxScore(createQuizzDTO.getMaxScore())
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

            // Cria a pergunta
            Question question = Question.builder()
                    .content(questionDTO.getContent())
                    .quizz(quizz)
                    .build();

            // Cria as respostas associadas à pergunta
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

        quizzRepository.save(quizz);
        return quizz;
    }

    private void validateAnswer(List<Answer> answers) {
        long correct = answers.stream()
                .filter(Answer::isCorrect)
                .count();

        if (correct != 1) {
            throw new IllegalArgumentException("Only one answer can be correct");
        }
    }

    public boolean toggleStatus(String slug) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Quizz quizz = quizzRepository.findBySlug(slug);
        if (quizz == null) {
            throw new ResourceNotFound("Quizz not found");
        }
        if (!quizz.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not allowed to toggle this status");
        }
        quizz.setStatus(!quizz.isStatus());
        quizzRepository.save(quizz);
        return true;
    }

//    public boolean checkIfAnswersAreValid(List<Answer> answers) {
//        for (Answer answer : answers) {
//            if (answer.isCorrect()) {
//                return true;
//            }
//        }
//        throw new IllegalArgumentException("At least one answer must be correct");
//    }
}
