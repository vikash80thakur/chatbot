package second.week.organization_service.service;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import second.week.organization_service.controller.ProjectController;
import second.week.organization_service.dto.context.ContextResponse;
import second.week.organization_service.dto.organization.OrganizationResponse;
import second.week.organization_service.dto.projects.ProjectResponse;
import second.week.organization_service.dto.users.UserResponse;
import second.week.organization_service.model.Organization;
import second.week.organization_service.model.Project;
import second.week.organization_service.model.User;
import second.week.organization_service.repository.OrganizationRepository;
import second.week.organization_service.repository.ProjectRepository;
import second.week.organization_service.repository.UserRepository;

import java.util.List;

@Service
public class ContextService {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ContextResponse getContext(Long orgId){
        Organization org = organizationRepository.findById(orgId);
        OrganizationResponse orgres = new OrganizationResponse();
        BeanUtils.copyProperties(org, orgres);

        // user Part
        List<User> users = userRepository.findByOrgId(orgId);
        List<UserResponse> userResponses = users.stream().map(user -> {
            UserResponse res = new UserResponse();
            BeanUtils.copyProperties(user, res);
            return res;
        }).toList();

        // Project part
        List<Project> projects = projectRepository.findByOrgId(orgId);
        List<ProjectResponse> projectResponses = projects.stream().map(project -> {
            ProjectResponse res = new ProjectResponse();
            BeanUtils.copyProperties(project, res);
            return res;
        }).toList();

        ContextResponse contextResponse = new ContextResponse();
        contextResponse.setOrganizationResponse(orgres);
        contextResponse.setUserResponses(userResponses);
        contextResponse.setProjectResponses(projectResponses);

        return contextResponse;
    }
}
