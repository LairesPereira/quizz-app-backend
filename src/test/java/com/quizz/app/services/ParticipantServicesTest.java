package com.quizz.app.services;

import com.quizz.app.Mocks.QuizzMockFactory;
import com.quizz.app.Mocks.UserMock;
import com.quizz.app.dto.ParticipantAnswerDTO;
import com.quizz.app.dto.ParticipantBasicInfoDTO;
import com.quizz.app.dto.QuizzCompletionDTO;
import com.quizz.app.errors.ForbiddenException;
import com.quizz.app.errors.ResourceNotFound;
import com.quizz.app.models.*;
import com.quizz.app.repositorie.ParticipantRepository;
import com.quizz.app.repositorie.QuizzRepository;
import com.quizz.app.utils.DefaultParticipantValues;
import com.quizz.app.utils.DefaultQuizzValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParticipantServicesTest {
    @Spy
    @InjectMocks
    ParticipantServices participantServices;

    @Mock
    QuizzRepository quizzRepository;

    @Mock
    ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        User user = UserMock.validModelUser();
        user.setId("1");
        user.setQuizzList(List.of(QuizzMockFactory.createQuizzModel()));

        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getPrincipal()).thenReturn(user);
        SecurityContext context = mock(SecurityContext.class);
        lenient().when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }


    @Test
    void testStartQuizz_CorrectData_ShouldReturn_QuizzStartedInfo() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();
        quizz.setId("1");
        quizz.setSlug("test");
        quizz.setStatus(true);


        when(quizzRepository.findBySlug("test")).thenReturn(Optional.of(quizz));
        when(participantRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = participantServices.startQuizz(ParticipantBasicInfoDTO.builder()
                .name("test")
                .email("test")
                .quizzSlug("test")
                .build()
        );

        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals("test", response.getQuizzSlug());
        assertNotNull(response.getQuestions());
        assertFalse(response.getQuestions().isEmpty());

        verify(quizzRepository).findBySlug("test");
        verify(participantRepository).save(any());
        verify(quizzRepository).save(any());
    }

    @Test
    void testStartQuizz_QuizzNotFound_ShouldThrowException() {
        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> participantServices.startQuizz(ParticipantBasicInfoDTO.builder()
                .quizzSlug(DefaultQuizzValues.INVALID_SLUG.getValue())
                .build()));
    }

    @Test
    void testStartQuizz_QuizzNotActive_ShouldThrowException() {
    Quizz quizz = QuizzMockFactory.createQuizzModel();
    quizz.setSlug(DefaultQuizzValues.VALID_SLUG.getValue());
    quizz.setStatus(false);

    when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.of(quizz));
    assertThrows(ForbiddenException.class, () -> participantServices.startQuizz(ParticipantBasicInfoDTO.builder()
            .quizzSlug(quizz.getSlug())
            .build()));
    }

    @Test
    void testStartQuizz_DuplicatEmail_ShouldThrowException() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();
        quizz.setSlug("test");
        quizz.setAllowDuplicateEmailOnQuizz(false);

        Participant participant = Participant.builder()
                        .email(DefaultParticipantValues.VALID_EMAIL.getValue())
                        .build();

        quizz.getParticipants().add(participant);

        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.of(quizz));

        assertThrows(ForbiddenException.class, () -> participantServices.startQuizz(
                ParticipantBasicInfoDTO.builder()
                        .email(DefaultParticipantValues.VALID_EMAIL.getValue())
                        .quizzSlug("test")
                        .build()
        ));
    }

    @Test
    void testSaveAnswers_ShouldSaveAnswers() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();

        Participant participant = Participant.builder()
                        .name(DefaultParticipantValues.NAME.getValue())
                        .email(DefaultParticipantValues.VALID_EMAIL.getValue())
                        .build();

        QuizzResult quizzResult = QuizzResult.builder()
                .quizz(quizz)
                .participant(participant)
                .build();

        quizz.getParticipants().add(participant);

        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.of(quizz));
        when(participantRepository.findById(any())).thenReturn(Optional.of(participant));


        QuizzCompletionDTO quizzCompletionDTO = QuizzCompletionDTO.builder()
                .quizzSlug(quizz.getSlug())
                .participantId(participant.getId())
                .answers(new ArrayList<>())
                .build();

        ParticipantAnswerDTO participantAnswerDTO = ParticipantAnswerDTO.builder()
                .questionId("1")
                .answerText("1")
                .build();

        quizzCompletionDTO.setAnswers(List.of(participantAnswerDTO));

        assertDoesNotThrow(() -> participantServices.saveAnswers(quizzCompletionDTO));
    }

    @Test
    void testSaveAnswers_QuizzNotFound_ShouldThrowException() {
        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class,
                () -> participantServices.saveAnswers(
                        QuizzCompletionDTO.builder()
                                .quizzSlug(DefaultQuizzValues.VALID_SLUG.getValue())
                                .build())
        );
    }

    @Test
    void testSaveAnswers_ParticipantNotFound_ShouldThrowException() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();

        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.of(quizz));
        when(participantRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class,
                () -> participantServices.saveAnswers(
                        QuizzCompletionDTO.builder()
                                .quizzSlug(DefaultQuizzValues.VALID_SLUG.getValue())
                                .participantId(DefaultParticipantValues.VALID_ID.getValue())
                                .build()
                )
        );
    }

    @Test
    void testStartQuizz_NotDuplicateEmail_NotThrowException() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();
        quizz.setAllowDuplicateEmailOnQuizz(false);

        Participant participant = Participant.builder()
                .status(true)
                .quizzes(new ArrayList<>())
                .name(DefaultParticipantValues.NAME.getValue())
                .email(DefaultParticipantValues.MISSMATCH_EMAIL.getValue())
                .build();

        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.of(quizz));
        when(participantRepository.save(participant)).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> participantServices.startQuizz(
                ParticipantBasicInfoDTO.builder()
                        .quizzSlug(DefaultQuizzValues.VALID_SLUG.getValue())
                        .email(DefaultParticipantValues.MISSMATCH_EMAIL.getValue())
                        .name(DefaultParticipantValues.NAME.getValue())
                        .build()
        ));

    }

    @Test
    void testSaveAnswers_QuizzNotActive_ShouldThrowException() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();
        quizz.setStatus(false);

        Participant participant = Participant.builder().build();

        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.of(quizz));
        when(participantRepository.findById(anyString())).thenReturn(Optional.of(participant));

        assertThrows(ForbiddenException.class,
                () -> participantServices.saveAnswers(
                        QuizzCompletionDTO.builder()
                                .quizzSlug(DefaultQuizzValues.VALID_SLUG.getValue())
                                .participantId(DefaultParticipantValues.VALID_ID.getValue())
                                .build()
                )
        );
    }

    @Test
    void testSaveAnswers_ParticipantNotInQuizz_ShouldThrowException() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();
        Participant participant = Participant.builder().build();

        quizz.setParticipants(new ArrayList<>());

        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.of(quizz));
        when(participantRepository.findById(anyString())).thenReturn(Optional.of(participant));

        assertThrows(ResourceNotFound.class,
                () -> participantServices.saveAnswers(
                        QuizzCompletionDTO.builder()
                                .quizzSlug(DefaultQuizzValues.VALID_SLUG.getValue())
                                .participantId(DefaultParticipantValues.VALID_ID.getValue())
                                .build()
                )
        );
    }

    @Test
    void testSaveAnswers_QuestionNotFoundInAnswers_ShouldThrowException() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();

        Participant participant = Participant.builder()
                .name(DefaultParticipantValues.NAME.getValue())
                .email(DefaultParticipantValues.VALID_EMAIL.getValue())
                .build();

        quizz.getParticipants().add(participant);

        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.of(quizz));
        when(participantRepository.findById(any())).thenReturn(Optional.of(participant));

        QuizzCompletionDTO quizzCompletionDTO = QuizzCompletionDTO.builder()
                .quizzSlug(quizz.getSlug())
                .participantId(participant.getId())
                .answers(new ArrayList<>())
                .build();

        ParticipantAnswerDTO participantAnswerDTO = ParticipantAnswerDTO.builder()
                .questionId("1")
                .answerText("1")
                .build();

        quizzCompletionDTO.setAnswers(List.of(participantAnswerDTO));
        quizz.setQuestions(new ArrayList<>());
        assertThrows(ResourceNotFound.class,
                () -> participantServices.saveAnswers(quizzCompletionDTO)
        );
    }

    @Test
    void testCalculateScore_ShouldReturnScore() {
        ParticipantServices service = new ParticipantServices();
        double score = service.calculateScore(
                List.of(ParticipantAnswerDTO.builder()
                        .questionId("1")
                        .answerText("1")
                        .build()),
                List.of(Question.builder()
                        .id("1")
                        .answer("1")
                        .answers(List.of(Answer.builder()
                                .id("1")
                                .answer("1")
                                .isCorrect(true)
                                .build()))
                        .content("1")
                        .build()),
                10.0);
    }

    @Test
    void testCalculateScore_ShouldNotMatchQuestionId() {
        ParticipantServices service = new ParticipantServices();

        double score = service.calculateScore(
                List.of(ParticipantAnswerDTO.builder()
                        .questionId("999")  // NÃO corresponde
                        .answerText("whatever")
                        .build()),
                List.of(Question.builder()
                        .id("1")            // ID diferente
                        .answer("1")
                        .answers(List.of(Answer.builder()
                                .id("1")
                                .answer("1")
                                .isCorrect(true)
                                .build()))
                        .content("1")
                        .build()),
                10.0);

        assertEquals(0.0, score);
    }

    @Test
    void testCalculateScore_ShouldNotEnterSecondIf() {
        double score = participantServices.calculateScore(
                List.of(ParticipantAnswerDTO.builder()
                        .questionId("1")
                        .answerText("X") // resposta errada
                        .build()),
                List.of(Question.builder()
                        .id("1")
                        .answer("1")
                        .answers(List.of(Answer.builder()
                                .id("1")
                                .answer("1")
                                .isCorrect(true)
                                .build()))
                        .content("1")
                        .build()),
                10.0);

        assertEquals(0.0, score);
    }
}
