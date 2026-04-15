package second.week.organization_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import second.week.organization_service.dto.projects.ProjectRequest;
import second.week.organization_service.dto.projects.ProjectResponse;
import second.week.organization_service.model.Project;
import second.week.organization_service.repository.ProjectRepository;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectResponse createProject(ProjectRequest request){

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOrganizationId(request.getOrganizationId());

        Project saved = projectRepository.save(project);

        ProjectResponse res = new ProjectResponse();
        BeanUtils.copyProperties(saved, res);

        return res;
    }

    public List<ProjectResponse> getAllProjects(){
        return projectRepository.findAll().stream().map(p -> {
            ProjectResponse res = new ProjectResponse();
            BeanUtils.copyProperties(p, res);
            return res;
        }).toList();
    }

    public List<ProjectResponse> getProjectsByOrgId(Long orgId){
        List<Project> projects = projectRepository.findByOrgId(orgId);
        return projects.stream().map(project -> {
            ProjectResponse res = new ProjectResponse();
            BeanUtils.copyProperties(project, res);
            return res;
        }).toList();
    }

    public ProjectResponse getProjectById(Long id){
        Project project = projectRepository.findById(id);
        ProjectResponse res = new ProjectResponse();
        BeanUtils.copyProperties(project, res);
        return res;
    }
}