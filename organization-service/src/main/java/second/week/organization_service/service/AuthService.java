package second.week.organization_service.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import second.week.organization_service.model.User;
import second.week.organization_service.repository.UserRepository;
import second.week.organization_service.util.JwtUtil;


@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private  final JwtUtil jwtUtil;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;


    public String login(String email, String password) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}
