package internship.chatbot_service.dto;

import internship.chatbot_service.dto.org.OrganizationResponse;
import internship.chatbot_service.dto.org.ProjectResponse;
import internship.chatbot_service.dto.org.UserResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContextResponse {

    private OrganizationResponse organizationResponse;
    private List<ProjectResponse> projectResponses;
    private List<UserResponse> userResponses;
}