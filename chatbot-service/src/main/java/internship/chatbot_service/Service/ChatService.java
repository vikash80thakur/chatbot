package internship.chatbot_service.Service;

import internship.chatbot_service.dto.ContextResponse;
import internship.chatbot_service.model.Conversation;
import internship.chatbot_service.model.Message;
import internship.chatbot_service.repository.ConversationRepository;
import internship.chatbot_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
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

    // 🔹 Create new conversation
    public Conversation createConversation(String user, Long orgId) {

        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setOrganizationId(orgId);
        conversation.setCreatedAt(LocalDateTime.now());

        return conversationRepository.save(conversation);
    }

    // 🔹 Send message
    public void sendMessage(Long conversationId, String content) {

        Message message = new Message();
        message.setConversationId(conversationId);
        message.setRole("USER");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
    }

    // 🔹 Get messages
    public List<Message> getMessages(Long conversationId) {
        return messageRepository.findByConversationId(conversationId);
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


    // below is for AI chat Response
    public String processMessage(Long orgId, String userMessage) {

        // 🔹 Step 1: Fetch context
        ContextResponse context = getContext(orgId);

        if (context == null) {
            return "Unable to fetch organization data.";
        }

        String msg = userMessage.toLowerCase();

        // 🔹 Step 2: Handle projects
        if (msg.contains("project")) {

            if (context.getProjectResponses() == null || context.getProjectResponses().isEmpty()) {
                return "No projects found.";
            }

            StringBuilder response = new StringBuilder("Projects:\n");

            context.getProjectResponses().forEach(p ->
                    response.append("- ").append(p.getName()).append("\n")
            );

            return response.toString();
        }

        // 🔹 Step 3: Handle users
        if (msg.contains("user")) {

            if (context.getUserResponses() == null || context.getUserResponses().isEmpty()) {
                return "No users found.";
            }

            StringBuilder response = new StringBuilder("Users:\n");

            context.getUserResponses().forEach(u ->
                    response.append("- ").append(u.getName()).append("\n")
            );

            return response.toString();
        }

        // 🔹 Step 4: Handle organization
        if (msg.contains("organization")) {

            if (context.getOrganizationResponse() == null) {
                return "Organization not found.";
            }

            return "Organization Name: " +
                    context.getOrganizationResponse().getName();
        }

        // 🔹 Step 5: Default response
        return "Sorry, I didn't understand your query.";
    }
}
