package second.week.organization_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import second.week.organization_service.model.Project;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class ProjectRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // CREATE
    public Project save(Project project){

        String sql = "INSERT INTO projects(name, description, organization_id) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setLong(3, project.getOrganizationId());
            return ps;
        }, keyHolder);

        project.setId(keyHolder.getKey().longValue());
        return project;
    }

    // GET ALL
    public List<Project> findAll(){
        return jdbcTemplate.query(
                "SELECT * FROM projects",
                (rs, rowNum) -> new Project(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getLong("organization_id")
                )
        );
    }

    public List<Project> findByOrgId(Long orgId){
        return jdbcTemplate.query(
                "SELECT * FROM projects WHERE organization_id=?",
                (rs, rowNum) -> new Project(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getLong("organization_id")
                ),
                orgId
        );
    }

    public Project findById(Long id){
        String sql = "SELECT * FROM projects WHERE id=?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Project(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getLong("organization_id")
        ), id);
    }

}