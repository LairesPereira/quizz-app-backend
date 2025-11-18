package com.quizz.app.services;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiApi {

    @Value("${gemini.api.key}")
    private String apiKey;

    public void sendPrompt() {
        System.err.println(apiKey);
        Client client = Client.builder()
                .apiKey(apiKey)
                .build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.0-flash",
                        "Gere uma alternativa errada, mas plausível, para a pergunta:\n" +
                                "\"Quem descobriu o Brasil e em que ano?\"\n" +
                                "\n" +
                                "A resposta deve ser:\n" +
                                "- curta (entre 25 e 50 palavras)\n" +
                                "- sem explicações\n" +
                                "- apenas a frase final\n" +
                                "- não adicionar comentários\n" +
                                "- resposta direta",
                        null);

        System.out.println(response.text());
    }
}
