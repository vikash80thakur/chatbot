package internship.chatbot_service.Service;

import internship.chatbot_service.dto.ContextResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.url}")
    private String url;


    public String askAI(String prompt) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

//        System.out.println("API KEY: " + apiKey);
//        System.out.println("URL: " + url);
//        System.out.println("MODEL: " + model);
//
//        System.out.println("OPENAI PROMPT:\n" + prompt);

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(message)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            List choices = (List) response.getBody().get("choices");
            Map first = (Map) choices.get(0);
            Map msg = (Map) first.get("message");

            return msg.get("content").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI service unavailable.";
        }
    }

}
