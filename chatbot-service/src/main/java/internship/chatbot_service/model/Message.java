package internship.chatbot_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private Long id;

    private Long conversationId;  // link to conversation

    private String role;          // USER / BOT

    private String content;       // actual message

    private LocalDateTime timestamp;
}