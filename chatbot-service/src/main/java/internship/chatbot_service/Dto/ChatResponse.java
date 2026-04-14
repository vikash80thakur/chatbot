package internship.chatbot_service.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatResponse {
    private String reply;

    public ChatResponse(String reply) {
        this.reply = reply;
    }
}
