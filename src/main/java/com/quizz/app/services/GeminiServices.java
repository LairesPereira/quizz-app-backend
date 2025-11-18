package com.quizz.app.services;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.quizz.app.dto.AlternativesWithAiDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiServices {
    private final int DEFAULT_INCORRECT_ALTERNATIVES = 3;
    private final int DEFAULT_CORRECT_ALTERNATIVES = 1;

    @Value("${gemini.api.key}")
    private String apiKey;

    public String sendPrompt(String text, String type) {
        Client client = Client.builder()
                .apiKey(apiKey)
                .build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.0-flash",
                        "Gere uma afimação " + type + " para:\n" +
                                "\"" + text + "\n" +
                                "\n" +
                                "A resposta deve ser:\n" +
                                "- curta (até 30 palavras)\n" +
                                "- sem explicações\n" +
                                "- apenas a frase final\n" +
                                "- não adicionar comentários\n" +
                                "- resposta direta",
                        null);

        System.out.println(response.text());
        return response.text();
    }

    public List<AlternativesWithAiDTO> generateAlternativesWithAi(String questionText) {
        List<AlternativesWithAiDTO> alternativesWithAiDTOList = new ArrayList<>();
        try {
            for (int i = 0; i < DEFAULT_INCORRECT_ALTERNATIVES; i++) {
                String incorrectAlternative = sendPrompt(questionText, "errada, mas que não seja tão óbvio que está errada");
                alternativesWithAiDTOList.add(AlternativesWithAiDTO.builder()
                        .content(incorrectAlternative)
                        .isCorrect(false)
                        .build());
            }
            for (int i = 0; i < DEFAULT_CORRECT_ALTERNATIVES; i++) {
                String correctAlternative = sendPrompt(questionText, "correta");
                alternativesWithAiDTOList.add(AlternativesWithAiDTO.builder()
                        .content(correctAlternative)
                        .isCorrect(true)
                        .build());
            }
            return alternativesWithAiDTOList;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
