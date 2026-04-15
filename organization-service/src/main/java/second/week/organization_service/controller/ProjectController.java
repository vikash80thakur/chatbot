package second.week.organization_service.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import second.week.organization_service.dto.projects.ProjectRequest;
import second.week.organization_service.dto.projects.ProjectResponse;
import second.week.organization_service.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ProjectResponse createProject(@Valid @RequestBody ProjectRequest request){
        return projectService.createProject(request);
    }

    @GetMapping
    public List<ProjectResponse> getAllProjects(){
        return projectService.getAllProjects();
    }


    @GetMapping("/orgs/{orgId}")
    public List<ProjectResponse> getProjectsByOrgId(@PathVariable Long orgId){
        return projectService.getProjectsByOrgId(orgId);
    }

    @GetMapping("/{projectId}")
    public ProjectResponse getProjectById(@PathVariable Long projectId){
        return projectService.getProjectById(projectId);
    }
}
