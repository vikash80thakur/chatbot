package second.week.organization_service.dto.users;

import lombok.*;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Long organizationId;
}