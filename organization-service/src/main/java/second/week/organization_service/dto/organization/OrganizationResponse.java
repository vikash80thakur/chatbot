package second.week.organization_service.dto.organization;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationResponse {
    private Long id;
    private String name;
    private String address;
}
