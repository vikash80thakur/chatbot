package internship.chatbot_service.dto.org;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long organizationId;
}