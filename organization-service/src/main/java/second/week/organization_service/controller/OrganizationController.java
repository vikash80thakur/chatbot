package second.week.organization_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import second.week.organization_service.dto.organization.OrganizationRequest;
import second.week.organization_service.dto.organization.OrganizationResponse;
import second.week.organization_service.service.OrganizationService;

import java.util.List;

@RestController
@RequestMapping("/orgs")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;


    @PostMapping
    public OrganizationResponse createOrg(@RequestBody OrganizationRequest request){
        return organizationService.createOrg(request);
    }

    @GetMapping
    public List<OrganizationResponse> getOrgs(){
        return organizationService.getOrgs();
    }

    @GetMapping("/{orgId}")
    public OrganizationResponse getOrgById(@PathVariable Long orgId){
        return organizationService.getOrgById(orgId);
    }

    @PutMapping("/{orgId}")
    public OrganizationResponse updateOrg(@PathVariable Long orgId, @RequestBody OrganizationRequest request){
        return organizationService.updateOrg(orgId, request);
    }

    @DeleteMapping("/{orgId}")
    public String deleteOrg(@PathVariable Long orgId){
        return organizationService.deleteOrg(orgId);
    }
}
