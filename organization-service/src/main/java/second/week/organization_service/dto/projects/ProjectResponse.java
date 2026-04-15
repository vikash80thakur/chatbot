package second.week.organization_service.dto.projects;

import lombok.*;

@Getter
@Setter
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long organizationId;
}