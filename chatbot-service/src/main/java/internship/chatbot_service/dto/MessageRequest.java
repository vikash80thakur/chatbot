package internship.chatbot_service.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private Long conversationId; 
    private String content;
}
