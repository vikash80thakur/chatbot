package second.week.organization_service.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "Name is must Required")
    private String name;

    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid email format")
    private String email;

    private Long organizationId;
}
