package internship.chatbot_service.dto;


import lombok.Data;

@Data
public class ChatRequest {
    private Long organizationId;
    private String message;
}
