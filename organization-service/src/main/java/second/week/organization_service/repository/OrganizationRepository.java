//package second.week.organization_service.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import second.week.organization_service.model.Organization;
//
//public interface OrganizationRepository extends JpaRepository<Organization, Long> {
//}


package second.week.organization_service.repository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import second.week.organization_service.model.Organization;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class OrganizationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // CREATE
    public Organization save(Organization org){

        String sql = "INSERT INTO organization(name, address) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, org.getName());
            ps.setString(2, org.getAddress());
            return ps;
        }, keyHolder);

        // 🔥 get generated ID
        Long generatedId = keyHolder.getKey().longValue();
        org.setId(generatedId);

        return org;
    }

    // GET ALL
    public List<Organization> findAll(){
        return jdbcTemplate.query(
                "SELECT * FROM organization",
                (rs, rowNum) -> new Organization(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("address")
                )
        );
    }

    // GET BY ID
    public Organization findById(Long id){
        return jdbcTemplate.queryForObject(
                "SELECT * FROM organization WHERE id=?",
                (rs, rowNum) -> new Organization(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("address")
                ),
                id
        );
    }

    // UPDATE
    public Organization update(Organization org){
        String sql = "UPDATE organization SET name=?, address=? WHERE id=?";
        int rows = jdbcTemplate.update(sql,
                org.getName(),
                org.getAddress(),
                org.getId()
        );

        if(rows == 0){
            throw new RuntimeException("Organization not found with Id: "+ org.getId());
        }

        Organization fetchedData = findById(org.getId());
        return fetchedData;
    }

    // DELETE
    public int delete(Long id){
        return jdbcTemplate.update(
                "DELETE FROM organization WHERE id=?",
                id
        );
    }
}