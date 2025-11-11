package com.quizz.app;

import com.quizz.app.config.TestSecurityConfig;
import com.quizz.app.dto.AuthenticationDTO;
import com.quizz.app.dto.LoginResponseDTO;
import com.quizz.app.dto.UserRegisterDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class QuizzControllerTests {

    private static final Logger logger = LoggerFactory.getLogger(QuizzControllerTests.class);

    @Autowired
    private TestRestTemplate restTemplate;

    private static UserRegisterDTO user;
    private static LoginResponseDTO loginResponse;

    @BeforeEach
    void setUp() {
        user = UserRegisterDTO.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@gmail.com")
                .password("123456")
                .build();

        restTemplate.postForEntity("/auth/register", user, Void.class);

        AuthenticationDTO login = new AuthenticationDTO(user.getEmail(), user.getPassword());
        ResponseEntity<LoginResponseDTO> response = restTemplate.postForEntity("/auth/login", login, LoginResponseDTO.class);
        loginResponse = response.getBody();
    }

    private static Stream<Arguments> provideInvalidFields() {
        return Stream.of(
                Arguments.of("title", "", "O titulo deve ter entre 3 e 1000 caracteres"),
                Arguments.of("description", "", "A descrição deve ter entre 3 e 10000 caracteres"),
                Arguments.of("maxScore", null, "O valor do score não deve ser nulo"),
                Arguments.of("questions", "[]", "O quiz deve conter pelo menos uma questão."),
                Arguments.of("questions[0].content", "", "A questão deve ter entre 1 e 1000 caracteres"),
                Arguments.of("questions[0].answers", "[]", "A questão deve conter pelo menos uma resposta."),
                Arguments.of("questions[0].answers[0].content", "", "A resposta deve ter entre 1 e 100000 caracteres")
        );
    }

    @Test
    void createQuizzWithRealAuthShouldReturnSuccess() {
        Assertions.assertNotNull(loginResponse);
        String token = loginResponse.token();
        String jsonQuizz = """
                {
                    "title": "Quiz de Teste",
                    "description": "Descrição do quiz de teste",
                    "maxScore": 10.0,
                    "questions": [
                        {
                            "content": "Qual é a cor do céu?",
                            "answers": [
                                {"content": "Azul", "isCorrect": true},
                                {"content": "Verde", "isCorrect": false}
                            ]
                        }
                    ]
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(jsonQuizz, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity("/quizz/create", request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFields")
    void createQuizzWithInvalidFieldShouldReturnBadRequest(String invalidField, Object invalidValue, String expectedMessage) {
        String token = loginResponse.token();

        String jsonQuizz = """
            {
                "title": "Quiz de Teste",
                "description": "Descrição do quiz de teste",
                "maxScore": 10.0,
                "questions": [
                    {
                        "content": "Qual é a cor do céu?",
                        "answers": [
                            {"content": "Azul", "isCorrect": true},
                            {"content": "Verde", "isCorrect": false}
                        ]
                    }
                ]
            }
            """;

        jsonQuizz = switch (invalidField) {
            case "title" ->
                    jsonQuizz.replace("\"title\": \"Quiz de Teste\"", "\"title\": \"\"");
            case "description" ->
                    jsonQuizz.replace("\"description\": \"Descrição do quiz de teste\"", "\"description\": \"\"");
            case "maxScore" ->
                    invalidValue == null
                            ? jsonQuizz.replace("\"maxScore\": 10.0", "\"maxScore\": null")
                            : jsonQuizz.replace("\"maxScore\": 10.0", "\"maxScore\": " + invalidValue);
            case "questions" ->
                    jsonQuizz.replaceFirst("\"questions\": \\[.*\\]", "\"questions\": []");
            case "questions[0].content" ->
                    jsonQuizz.replace("\"content\": \"Qual é a cor do céu?\"", "\"content\": \"\"");
            case "questions[0].answers" ->
                    jsonQuizz.replaceFirst("\"answers\": \\[.*\\]", "\"answers\": []");
            case "questions[0].answers[0].content" ->
                    jsonQuizz.replace("\"content\": \"Azul\"", "\"content\": \"\"");
            default -> jsonQuizz;
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(jsonQuizz, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("/quizz/create", request, Map.class);

        logger.info("Field tested: {} | Response body: {}", invalidField, response.getBody());

        assertThat(response.getStatusCode())
                .withFailMessage("Falhou no campo: %s (valor: %s)", invalidField, invalidValue)
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo(expectedMessage);
    }
}
