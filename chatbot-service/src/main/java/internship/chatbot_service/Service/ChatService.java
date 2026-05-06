package internship.chatbot_service.Service;

import internship.chatbot_service.dto.AsyncResponse;
import internship.chatbot_service.dto.ContextResponse;
import internship.chatbot_service.dto.MessageRequest;
import internship.chatbot_service.dto.QueueMessage;
import internship.chatbot_service.model.Conversation;
import internship.chatbot_service.model.Message;
import internship.chatbot_service.repository.ConversationRepository;
import internship.chatbot_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    private final RestTemplate restTemplate;

    private final OpenAIService openAIService;


    // rabbitMQ configuration
    @Autowired
    private final RabbitMQProducer producer;

    // 🔹 Create new conversation
    public Conversation createConversation(String user, Long orgId) {

        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setOrganizationId(orgId);
        conversation.setCreatedAt(LocalDateTime.now());

        return conversationRepository.save(conversation);
    }

    // 🔹 Send message
    public void sendMessage(MessageRequest messageRequest) {

        Message message = new Message();
        message.setConversationId(messageRequest.getConversationId());
        message.setRole("USER");
        message.setContent(messageRequest.getContent());
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
    }

    // 🔹 Get messages
    public List<Message> getMessages(Long conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }


    // 🔹 Delete single message
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }

    // 🔹 Delete all messages of a conversation
    public void deleteMessagesByConversation(Long conversationId) {
        messageRepository.deleteByConversationId(conversationId);
    }

    // 🔹 Update message
    public void updateMessage(Long id, String content) {
        messageRepository.updateContent(id, content);
    }


    // this is for context api
    public ContextResponse getContext(Long orgId) {

        String url = "http://localhost:8082/internal/context/" + orgId;

        try {
            System.out.println(restTemplate.getForObject(url, String.class));
            return restTemplate.getForObject(url, ContextResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("ORG_SERVICE_DOWN");
        }
    }




    private String buildPrompt(ContextResponse context, String message, String user, Long conversationId) {

        StringBuilder prompt = new StringBuilder();

        String name = user.split("@")[0]; // simple personalization

        List<Message> history =
                messageRepository.findByConversationId(conversationId);

        if (history.size() > 10) {
            history = history.subList(history.size() - 10, history.size());
        }

        prompt.append("You are a helpful AI assistant.\n\n");

        prompt.append("User Name: ").append(name).append("\n");
        prompt.append("User Email: ").append(user).append("\n\n");

        if (context.getOrganizationResponse() != null) {
            prompt.append("Organization: ")
                    .append(context.getOrganizationResponse().getName())
                    .append("\n");
        }
        if (context.getProjectResponses() != null) {
            prompt.append("Projects:\n");
            context.getProjectResponses().forEach(p ->
                    prompt.append("- ").append(p.getName()).append("\n"));
        }

        if (context.getUserResponses() != null) {
            prompt.append("Team Members:\n");
            context.getUserResponses().forEach(u ->
                    prompt.append("- ").append(u.getName()).append("\n"));
        }

        prompt.append("\nInstructions:\n");
        prompt.append("- Respond like a smart assistant\n");
        prompt.append("- Be friendly and professional\n");
        prompt.append("- Use user's name when appropriate\n");
        prompt.append("- Keep answer clear and natural\n");
        prompt.append("- Do NOT make up data\n");

        prompt.append("\nResponse Guidelines:\n");
        prompt.append("- Respond in a friendly and professional tone\n");
        prompt.append("- Use bullet points when listing multiple items\n");
        prompt.append("- Use short paragraphs for explanations\n");
        prompt.append("- Keep the response clear, clean, and easy to read\n");
        prompt.append("- Do NOT use markdown (no ###, no **)\n");
        prompt.append("- Use simple bullet points like • or -\n");
        prompt.append("- Do not repeat information unnecessarily\n");

        prompt.append("\nConversation History:\n");
        for (Message msg : history) {
            prompt.append(msg.getRole())  // "user" or "assistant"
                    .append(": ")
                    .append(msg.getContent())
                    .append("\n");
        }

        prompt.append("\nUser Question: ").append(message);

        return prompt.toString();
    }

    // below is for AI chat Response
    public AsyncResponse processMessage(Long conversationId, Long orgId, String message, String user) {

        ContextResponse context = getContext(orgId);

//        System.out.println("CALLING OPENAI...");

        // 🔹 AI fallback
        String prompt = buildPrompt(context, message, user, conversationId);

        // this is previous one
//        return openAIService.askAI(prompt);


        // this is for rabbitmQ
        QueueMessage queueMessage = new QueueMessage(conversationId, prompt);
        producer.sendMessage(queueMessage);
//        producer.sendMessage(prompt);
        return new AsyncResponse(
                "PROCESSING",
                conversationId
        );
    }
}
