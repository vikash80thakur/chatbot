package internship.chatbot_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AsyncResponse {

    private String status;
    private Long conversationId;
}