package second.week.organization_service.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Long organizationId;

//    public User(long id, String name, String email, long organizationId) {
//    }
}
