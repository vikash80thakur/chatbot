package second.week.organization_service.dto.context;

import lombok.Getter;
import lombok.Setter;
import second.week.organization_service.dto.organization.OrganizationResponse;
import second.week.organization_service.dto.projects.ProjectResponse;
import second.week.organization_service.dto.users.UserResponse;

import java.util.List;


@Getter
@Setter
public class ContextResponse {
     private OrganizationResponse organizationResponse;
     private List<UserResponse> userResponses;
     private List<ProjectResponse> projectResponses;
}
