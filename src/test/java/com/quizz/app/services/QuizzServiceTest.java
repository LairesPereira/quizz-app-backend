package com.quizz.app.services;

import com.quizz.app.Mocks.QuizzMockFactory;
import com.quizz.app.Mocks.UserMock;
import com.quizz.app.dto.CreateQuizzDTO;
import com.quizz.app.dto.ParticipantQuizzInfo;
import com.quizz.app.dto.StatisctsResopnseDTO;
import com.quizz.app.errors.ForbiddenException;
import com.quizz.app.errors.ResourceNotFound;
import com.quizz.app.models.*;
import com.quizz.app.repositorie.ParticipantRepository;
import com.quizz.app.repositorie.QuestionAndAnswerRepository;
import com.quizz.app.repositorie.QuizzRepository;
import com.quizz.app.repositorie.QuizzResultRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizzServiceTest {

    @Spy
    @InjectMocks
    private QuizzServices quizzServices;

    @Mock
    private QuizzRepository quizzRepository;

    @Mock
    private QuizzResultRepository quizzResultRepository;

    @Mock
    private QuestionAndAnswerRepository questionAndAnswerRepository;

    @Mock
    private ParticipantRepository participantRepository;

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
    void testToggleStatusShouldToggleAndReturnTrue() {
        User owner = new User();
        owner.setId("1"); // mesmo ID do BeforeEach

        Quizz quizz = new Quizz();
        quizz.setSlug("test");
        quizz.setUser(owner);
        quizz.setStatus(false);

        when(quizzRepository.findBySlug("test")).thenReturn(Optional.of(quizz));
        when(quizzRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        boolean result = quizzServices.toggleStatus("test");

        assertTrue(result);
        assertTrue(quizz.isStatus());
    }

    @Test
    void testToggleStatusWithNonExistentSlug() {
        when(quizzRepository.findBySlug("test")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> quizzServices.toggleStatus("test"));
    }

    @Test
    void testToggleStatusWithWrongUserIdShouldFail() {
        User user = new User();
        user.setId("2");

        Quizz quizz = QuizzMockFactory.createQuizzModel();
        quizz.setUser(user);

        when(quizzRepository.findBySlug("test")).thenReturn(Optional.of(quizz));
        when(quizzRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        quizzRepository.save(quizz);

        assertThrows(ForbiddenException.class, () -> quizzServices.toggleStatus("test"));
    }

    @Test
    void testCreateQuizzWithNoCorrectAnswerShouldFail() {
        CreateQuizzDTO quizz = QuizzMockFactory.createSimpleQuizz();
        quizz.getQuestions().get(0).getAnswers().get(0).setCorrect(false);
        quizz.getQuestions().get(0).getAnswers().get(1).setCorrect(false);
        assertThrows(IllegalArgumentException.class, () -> quizzServices.save(quizz));
    }

    @Test
    void testCreateQuizzWithTwoOrMoreCorrectAnswerShouldFail() {
        CreateQuizzDTO quizz = QuizzMockFactory.createSimpleQuizz();
        quizz.getQuestions().get(0).getAnswers().get(0).setCorrect(true);
        quizz.getQuestions().get(0).getAnswers().get(1).setCorrect(true);
        assertThrows(IllegalArgumentException.class, () -> quizzServices.save(quizz));
    }

    @Test
    void testCreateQuizzWithOneCorrectAnswerShouldNotThrowException() {
        CreateQuizzDTO quizz = QuizzMockFactory.createSimpleQuizz();
        quizz.getQuestions().get(0).getAnswers().get(0).setCorrect(true);
        quizz.getQuestions().get(0).getAnswers().get(1).setCorrect(false);
        assertDoesNotThrow(() -> quizzServices.save(quizz));
    }

    @Test
    void testFindAllByUserIdShouldReturnListWithQuizzes() {
        User user = new User();
        user.setId("1");

        List<Quizz> quizzes = List.of(
                Quizz.builder().build(),
                Quizz.builder().build()
        );
        user.setQuizzList(quizzes);

        when(quizzRepository.findAllByUserId("1")).thenReturn(quizzes);
        assertNotNull(quizzServices.findAll());
    }

    @Test
    void testFindAllByUserIdShouldReturnEmptyList() {
        User user = new User();
        user.setId("1");

        List<Quizz> quizzes = new ArrayList<>();
        user.setQuizzList(quizzes);

        when(quizzRepository.findAllByUserId("1")).thenReturn(quizzes);
        assertTrue(quizzServices.findAll().isEmpty());
    }

    @Test
    void testGetParticipantsBasicInfoByQuizzSlug() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();
        quizz.setId("1");
        quizz.setSlug("test");
        quizz.setParticipants(List.of(
                Participant.builder().id("1").build(),
                Participant.builder().id("2").build()
        ));

        when(quizzRepository.findBySlug("test")).thenReturn(Optional.of(quizz));
        doReturn(1.0).when(quizzServices).getParticipantScoreByQuizzSlug(anyString(), anyString());

        List<ParticipantQuizzInfo> participantQuizzInfoList = quizzServices.getParticipantsBasicInfoByQuizzSlug("test");
        assertFalse(participantQuizzInfoList.isEmpty());
    }

    @Test
    void testGetParticipantBasicInfoWithNoQuizzSlugShouldThrowException() {
        assertThrows(ResourceNotFound.class, () -> quizzServices.getParticipantsBasicInfoByQuizzSlug("any"));
    }

    @Test
    void testQuizzFindBySlugShouldReturnQuizz() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();
        quizz.setId("1");
        quizz.setSlug("test");

        when(quizzRepository.findBySlug("test")).thenReturn(Optional.of(quizz));
        assertNotNull(quizzServices.findBySlug("test"));
    }

    @Test
    void testQuizzFindBySlugShouldThrowException() {
        assertThrows(ResourceNotFound.class, () -> quizzServices.findBySlug("any"));
    }

    @Test
    void getParticipantScoreByQuizzSlugShouldReturnScore() {
        QuizzResult quizzResult = QuizzResult.builder()
                .score(1.)
                .build();
        when(quizzResultRepository.findByParticipantIdAndQuizzId("1", "1")).thenReturn(Optional.of(quizzResult));
        assertEquals(1., quizzServices.getParticipantScoreByQuizzSlug("1", "1"));
    }

    @Test
    void testGetParticipantResultScoreNullShouldThrowException() {
        QuizzResult quizzResult = QuizzResult.builder()
                .score(null)
                .build();
        when(quizzResultRepository.findByParticipantIdAndQuizzId("1", "1")).thenReturn(Optional.of(quizzResult));
        assertThrows(ResourceNotFound.class, () -> quizzServices.getParticipantScoreByQuizzSlug("1", "1"));
    }

    @Test
    void getParticipantScoreByQuizzSlugShouldThrowException() {
        assertThrows(ResourceNotFound.class, () -> quizzServices.getParticipantScoreByQuizzSlug("30", "30"));
    }

    @Test
    void getMaxScoreBySlug() {
        Quizz quizz = QuizzMockFactory.createQuizzModel();
        quizz.setId("1");
        quizz.setSlug("test");
        quizz.setMaxScore(1.);

        when(quizzRepository.findBySlug("test")).thenReturn(Optional.of(quizz));
        assertEquals(1., quizzServices.getMaxScoreBySlug("test"));
    }

    @Test
    void getMaxScoreByNonExistingSlugShouldThrowException() {
        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> quizzServices.getMaxScoreBySlug(anyString()));
    }

    @Test
    void testGetParticipantResultByQuizzSlugAndParticipantId() {
        Quizz quizz = Quizz.builder().id("1").build();
        Participant participant = Participant.builder().id("1").build();
        QuizzResult quizzResult = QuizzResult.builder().id("1").score(1.).build();
        QuestionAndAnswer qa = QuestionAndAnswer.builder().id("1").build();

        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.of(quizz));
        when(participantRepository.findById(anyString())).thenReturn(Optional.of(participant));
        when(quizzResultRepository.findByParticipantIdAndQuizzId(anyString(), anyString())).thenReturn(Optional.of(quizzResult));
        when(questionAndAnswerRepository.findByQuizzResultId(anyString())).thenReturn(List.of(qa));

        assertNotNull(quizzServices.getParticipantResultByQuizzSlugAndParticipantId("1", "1"));
    }

    @Test
    void testGetParticipantResultByQuizzSlugAndParticipantIdShouldThrowException() {
        assertThrows(ResourceNotFound.class, () -> quizzServices.getParticipantResultByQuizzSlugAndParticipantId("30", "30"));
    }

    @Test
    void testGetParticipantResultByQuizzSlugAndParticipantId_WithInexistentParticipantId_ShouldThrowException() {
        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.of(Quizz.builder().build()));
        when(participantRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> quizzServices.getParticipantResultByQuizzSlugAndParticipantId("30", "30"));
    }

    @Test
    void testGetParticipantResultByQuizzSlugAndParticipantId_WithInexistentQuizzSlug_ShouldThrowException() {
        when(quizzRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> quizzServices.getParticipantResultByQuizzSlugAndParticipantId("30", "30"));
    }

    @Test
    void getParticipantResult_InexistentQuizzResult_ShouldThrow() {
        Quizz quizz = Quizz.builder().id("30").build();
        Participant participant = Participant.builder().id("30").build();

        when(quizzRepository.findBySlug("30")).thenReturn(Optional.of(quizz));
        when(participantRepository.findById("30")).thenReturn(Optional.of(participant));
        when(quizzResultRepository.findByParticipantIdAndQuizzId("30", "30"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class,
                () -> quizzServices.getParticipantResultByQuizzSlugAndParticipantId("30", "30"));
    }

    @Test
    void getStatistics() {
        assertNotNull(quizzServices.getStatistics());
    }

    @Test
    void getStatistics_WithNoQuizzes_ShouldThrowException() {
        User user = User.builder()
                .id("1")
                .quizzList(Collections.emptyList())
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);

        assertThrows(ResourceNotFound.class,
                () -> quizzServices.getStatistics());
    }



    @Test
    void getStatistics_ShouldCalculateSumAndMeanScoresCorrectly() {
        // --- Setup do usuário ---
        User user = User.builder()
                .id("1")
                .quizzList(new ArrayList<>())
                .build();

        // Quiz com 2 participantes
        Participant p1 = Participant.builder().id("p1").build();
        Participant p2 = Participant.builder().id("p2").build();

        Quizz quizz = Quizz.builder()
                .participants(List.of(p1, p2))
                .build();

        user.getQuizzList().add(quizz);

        // Mock do contexto de segurança
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);

        // --- Mock dos resultados dos participantes ---
        when(quizzResultRepository.findAllByParticipantId("p1"))
                .thenReturn(List.of(
                        QuizzResult.builder().score(4.0).build(),
                        QuizzResult.builder().score(6.0).build()
                ));

        when(quizzResultRepository.findAllByParticipantId("p2"))
                .thenReturn(List.of(
                        QuizzResult.builder().score(10.0).build()
                ));

        // --- Execução ---
        StatisctsResopnseDTO result = quizzServices.getStatistics();

        // --- Verificações ---
        assertEquals(1, result.getTotalQuizzes()); // apenas um quiz
        assertEquals(2, result.getTotalParticipants()); // dois participantes
        assertEquals((4.0 + 6.0 + 10.0) / 2, result.getMeanScore()); // sumScores / totalParticipants
    }

    @Test
    void toggleStatus_ShouldExecuteSetStatus() {
        // Mock do usuário autenticado
        User user = new User();
        user.setId("abc");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        // Mock do quizz pertencente ao usuário
        Quizz quizz = new Quizz();
        quizz.setId("q1");
        quizz.setUser(user);   // ← ESSENCIAL: mesmo objeto, não apenas mesmo ID
        quizz.setStatus(false);

        when(quizzRepository.findBySlug("slug"))
                .thenReturn(Optional.of(quizz));

        // Executa
        quizzServices.toggleStatus("slug");

        // Confirma que o setStatus foi executado
        assertTrue(quizz.isStatus());
    }

}


