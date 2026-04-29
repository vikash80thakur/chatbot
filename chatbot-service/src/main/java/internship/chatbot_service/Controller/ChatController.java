package internship.chatbot_service.Controller;

import internship.chatbot_service.Service.ChatService;
import internship.chatbot_service.dto.ChatRequest;
import internship.chatbot_service.dto.ContextResponse;
import internship.chatbot_service.dto.MessageRequest;
import internship.chatbot_service.model.Conversation;
import internship.chatbot_service.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 🔹 Create conversation
    @PostMapping("/conversations/{orgId}")
    public Conversation createConversation(
            @RequestHeader("X-User") String user,
            @PathVariable Long orgId
    ) {
        return chatService.createConversation(user, orgId);
    }

    // 🔹 Send message
    @PostMapping("/conversations/{conversationId}/messages")
    public String sendMessage(
            @PathVariable Long conversationId,
            @RequestBody MessageRequest request
    ) {
        chatService.sendMessage(conversationId, request.getContent());
        return "Message saved successfully";
    }

    // 🔹 Get messages
    @GetMapping("/messages/{conversationId}")
    public List<Message> getMessages(@PathVariable Long conversationId) {
        return chatService.getMessages(conversationId);
    }



    // rest template
    @GetMapping("/context/{orgId}")
    public ContextResponse getContext(@PathVariable Long orgId) {
        return chatService.getContext(orgId);
    }

    @PostMapping("/ai")
    public String chat(
            @RequestHeader("X-User") String user,
            @RequestBody ChatRequest request
    ) {
        return chatService.processMessage(
                request.getOrganizationId(),
                request.getMessage()
        );
    }
}
