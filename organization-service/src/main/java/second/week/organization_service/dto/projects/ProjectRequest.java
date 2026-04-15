package second.week.organization_service.dto.projects;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class ProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    private Long organizationId;
}