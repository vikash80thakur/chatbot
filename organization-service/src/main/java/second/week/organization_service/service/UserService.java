package second.week.organization_service.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import second.week.organization_service.dto.users.UserRequest;
import second.week.organization_service.dto.users.UserResponse;
import second.week.organization_service.model.User;
import second.week.organization_service.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest request){
        logger.info("Creating user with email: {}", request.getEmail());
        if(userRepository.countByMail(request.getEmail()) > 0){
            logger.error("User already exists with email: {}", request.getEmail());
            throw new RuntimeException("Email already Exits: Please try another One: ");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
//        Here I'm saving password in encrypted form
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setOrganizationId(request.getOrganizationId());

        User savedOne = userRepository.save(user);
        logger.info("User created successfully with id: {}", savedOne.getId());

        UserResponse res = new UserResponse();
        BeanUtils.copyProperties(savedOne, res);


        return res;

    }

    // GET ALL
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll().stream().map(user -> {
            UserResponse res = new UserResponse();
            BeanUtils.copyProperties(user, res);
            return res;
        }).toList();
    }

    // GET BY ID
    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id);

        if(user == null){
            throw new RuntimeException("User not found with id: " + id);
        }

        UserResponse res = new UserResponse();
        BeanUtils.copyProperties(user, res);
        return res;
    }

    // UPDATE
    public UserResponse updateUser(Long id, UserRequest request){

        User user = userRepository.findById(id);

        if(user == null){
            throw new RuntimeException("User not found");
        }

        // check email uniqueness (if changed)
        if(!user.getEmail().equals(request.getEmail())
                && userRepository.countByMail(request.getEmail()) > 0){
            throw new RuntimeException("Email already exists");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        userRepository.update(user);

        UserResponse res = new UserResponse();
        BeanUtils.copyProperties(user, res);

        return res;
    }

    // DELETE
    public String deleteUser(Long id){

        User user = userRepository.findById(id);

        if(user == null){
            throw new RuntimeException("User not found");
        }

        userRepository.delete(id);

        return "User deleted successfully";
    }
}
