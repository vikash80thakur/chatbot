package internship.chatbot_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {

    private Long id;

    private String user;          // from X-User header

    private Long organizationId;  // needed to fetch context

    private LocalDateTime createdAt;
}
