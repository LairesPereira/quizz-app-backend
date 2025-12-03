package com.quizz.app.Mocks;

import com.quizz.app.dto.AnswerDTO;
import com.quizz.app.dto.CreateQuizzDTO;
import com.quizz.app.dto.QuestionDTO;
import com.quizz.app.models.Answer;
import com.quizz.app.models.Participant;
import com.quizz.app.models.Question;
import com.quizz.app.models.Quizz;
import com.quizz.app.utils.DefaultParticipantValues;
import com.quizz.app.utils.DefaultQuizzValues;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class QuizzMockFactory {

    public static CreateQuizzDTO createSimpleQuizz() {
        return CreateQuizzDTO.builder()
                .title("Teste")
                .description("Descrição do quiz de teste")
                .maxScore(10.0)
                .isMobileAllowed(true)
                .allowUserSeeResults(true)
                .allowDuplicateEmailOnQuizz(true)
                .questions(List.of(createSimpleQuestion()))
                .build();
    }

    public static QuestionDTO createSimpleQuestion() {
        return QuestionDTO.builder()
                .content("Qual é a cor do céu?")
                .answers(List.of(
                        createAnswer("Azul", false),
                        createAnswer("Verde", true)
                ))
                .build();
    }

    public static AnswerDTO createAnswer(String content, boolean isCorrect) {
        return AnswerDTO.builder()
                .content(content)
                .isCorrect(isCorrect)
                .build();
    }

    public static CreateQuizzDTO createQuizzWithInvalidField() {
        CreateQuizzDTO quizz = createSimpleQuizz();
        quizz.setTitle(null);
        return quizz;
    }

    public static Quizz createQuizzModel() {
        return Quizz.builder()
                .id("1")
                .title(DefaultQuizzValues.TITLE.getValue())
                .description(DefaultQuizzValues.DESCRIPTION.getValue())
                .slug(DefaultQuizzValues.VALID_SLUG.getValue())
                .maxScore(10.0)
                .status(true)
                .isMobileAllowed(true)
                .allowUserSeeResults(true)
                .allowDuplicateEmailOnQuizz(true)
                .participants(createParticipants())
                .questions(createQuestionModel())
                .build();

    }

    private static List<Participant> createParticipants() {
            List<Participant> participants = new ArrayList<>();
            participants.add(Participant.builder()
                    .id("1")
                    .email(DefaultParticipantValues.VALID_EMAIL.getValue())
                    .build());
            return participants;
    }

    private static List<Question> createQuestionModel() {
        return List.of(
                Question.builder()
                        .id("1")
                        .content("1")
                        .answer("1")
                        .answers(createAnswersModel())
                        .build()
        );
    }

    private static List<Answer> createAnswersModel() {
        return List.of(
                Answer.builder()
                        .id("1")
                        .answer("1")
                        .isCorrect(true)
                        .build()
        );
    }
}
