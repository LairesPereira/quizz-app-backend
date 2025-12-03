package com.quizz.app.services;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.quizz.app.dto.AlternativesWithAiDTO;
import lombok.Generated;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Generated
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
                        "Gere uma afirmação " + type + " para a seguinte pergunta:\n" +
                                "\"" + text + "\"\n\n" +
                                "Regras IMPORTANTES:\n" +
                                "- A alternativa deve ter até 15 palavras.\n" +
                                "- Não repita o enunciado da pergunta.\n" +
                                "- Não explique nada, responda só a alternativa.\n" +
                                "- Não adicione comentários.\n" +
                                "- A alternativa deve parecer realista e coerente com o tema.\n" +
                                "- A alternativa não pode ser semelhante às outras alternativas.\n" +
                                "- Evite frases vagas como 'Não sei', 'Nenhuma das anteriores', etc.\n\n" +
                                "Para alternativas incorretas:\n" +
                                "- Deve ser PLAUSÍVEL, porém incorreta.\n" +
                                "- O erro deve ser sutil, como trocar uma data, local, número ou conceito.\n" +
                                "- Não invente fatos completamente absurdos ou impossíveis.\n" +
                                "- Não gere algo que possa ser interpretado como correto.\n\n" +
                                "Para a alternativa correta:\n" +
                                "- Gere a resposta factual correta e objetiva.\n\n" +
                                "Responda apenas a alternativa.",
        null);

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

            Collections.shuffle(alternativesWithAiDTOList);
            return alternativesWithAiDTOList;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public AlternativesWithAiDTO generateSingleAlternativesWithAi(String questionText) {
        try {
            return AlternativesWithAiDTO.builder()
                    .content(sendPrompt(questionText, "incorreta"))
                    .isCorrect(false)
                    .build();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
