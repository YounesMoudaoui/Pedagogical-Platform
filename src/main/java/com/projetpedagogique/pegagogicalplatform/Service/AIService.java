package com.projetpedagogique.pegagogicalplatform.Service;

import com.projetpedagogique.pegagogicalplatform.Util.PdfReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class AIService {
    @Value("${openai.api.key}")
    private String openAiApiKey;
    @Value("${openai.api.url}")
    private String openAiApiUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    // Génération des questions à partir d'un fichier PDF
    public String generateQuestions(String pdfFilePath) {
        try {
            // Étape 1 : Extraire le texte du fichier PDF
            String pdfContent = PdfReader.extractTextFromPdf(pdfFilePath);
            // Limiter la longueur du contenu PDF pour éviter de dépasser la limite de token
            if (pdfContent.length() > 3000) {
                pdfContent = pdfContent.substring(0, 3000) + "...";
            }
            // Étape 2 : Préparer les en-têtes de requête API OpenAI
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiApiKey);
            headers.set("Content-Type", "application/json");
            // Étape 3 : Créer le corps de la requête avec une meilleure consigne (prompt)
            String prompt = "Based on the following text, generate 20 multiple-choice questions with exactly 4 options (A, B, C, D)." +
                    " Each question should be numbered and presented on a new line in the following format:" +
                    " '<number>. <question>' (e.g., '20. What is the key restriction imposed by Oracle regarding the course materials?')." +
                    " After the question, list the four options on separate lines, prefixed by A), B), C), and D)." +
                    " Clearly mark the correct answer with 'Réponse:' followed by only the letter of the correct option (A, B, C, or D)." +
                    " Do not include the full text of the correct option, just the letter." +
                    " Here is the text:\n\n" + pdfContent;



            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            requestBody.put("messages", List.of(userMessage));
            requestBody.put("max_tokens", 1500);
            requestBody.put("temperature", 0.7);

            // Étape 4 : Envoyer la requête à OpenAI
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(openAiApiUrl, HttpMethod.POST, entity, Map.class);
            System.out.println(response);

            // Étape 5 : Vérifier si la réponse contient les questions générées
            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");

                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String generatedText = (String) message.get("content");

                    if (generatedText != null) {
                        return generatedText.trim();
                    } else {
                        throw new IllegalArgumentException("Le texte généré est vide. Impossible d'analyser les questions.");
                    }
                }
            }
            throw new IllegalArgumentException("L'API OpenAI n'a pas renvoyé de texte généré.");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
