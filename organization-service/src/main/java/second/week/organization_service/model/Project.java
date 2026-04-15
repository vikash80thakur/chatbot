package second.week.organization_service.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    private Long id;
    private String name;
    private String description;
    private Long organizationId;
}