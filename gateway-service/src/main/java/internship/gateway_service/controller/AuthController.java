package internship.gateway_service.controller;


import internship.gateway_service.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/token")
    public String getToken(@RequestParam String username){
        return jwtUtil.generateToken(username);
    }
}
