package second.week.organization_service.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import second.week.organization_service.model.User;

import java.sql.PreparedStatement;
import java.util.List;


@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate template;

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    // for creating user
    public User save(User user){
        logger.debug("Saving user to database: {}", user.getEmail());
        String sql = "INSERT INTO users (name, email, organization_id) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setLong(3, user.getOrganizationId());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        user.setId(generatedId);

        logger.debug("User saved with id: {}", user.getId());
        return user;
    }

    // for getting all users
    public List<User> findAll(){
        String sql = "SELECT * FROM users";
        return template.query(sql, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getLong("organization_id")
        ));
    }

    // for getting User by id
    public User findById(Long id){
        String sql = "SELECT * FROM users where id=?";
        return template.query(sql, rs -> rs.next() ? new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getLong("organization_id")
        ): null, id);
    }


    // for updating the user
    public User update(User user){
        String sql = "UPDATE users SET name=?, email=? WHERE id=?";
        int row =  template.update(sql, user.getName(), user.getEmail(), user.getId());
        if(row == 0){
            throw new RuntimeException("User not Found: " + user.getId());
        }

        return findById(user.getId());
    }

    // delete the user
    public int delete(Long id){
        return template.update("DELETE FROM users WHERE id=?", id);
    }

    // CHECK MAIL
    public Integer countByMail(String email){
        return template.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email=?",
                Integer.class,
                email
        );
    }

    public List<User> findByOrgId(Long orgId){
        String sql = "SELECT * FROM users WHERE organization_id=?";
        return template.query(sql, ((rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getLong("organization_id")
        )), orgId);
    }
}
