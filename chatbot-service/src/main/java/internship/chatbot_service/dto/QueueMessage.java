package internship.chatbot_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QueueMessage {
    private Long conversationId;
    private String prompt;
}
