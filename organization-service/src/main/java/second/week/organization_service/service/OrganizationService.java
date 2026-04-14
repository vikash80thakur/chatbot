package second.week.organization_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import second.week.organization_service.dto.organization.OrganizationRequest;
import second.week.organization_service.dto.organization.OrganizationResponse;
import second.week.organization_service.exception.ResourceNotFoundException;
import second.week.organization_service.model.Organization;
import second.week.organization_service.repository.OrganizationRepository;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;


    //First One: For creating Organization
    public OrganizationResponse createOrg(@RequestBody OrganizationRequest request){
        // converting DTO To Entity
        Organization org = new Organization();
        if(request.getAddress().isEmpty() || request.getName().isEmpty()){
            throw new RuntimeException("Please Provide Name and Address");
        }
        org.setName(request.getName());
        org.setAddress(request.getAddress());

        Organization savedOrg = organizationRepository.save(org);

        // Entity to DTO Response
        OrganizationResponse res = new OrganizationResponse();
        BeanUtils.copyProperties(savedOrg, res);
        return res;
    }

    // Second Service: Get All Organizations
    public List<OrganizationResponse> getOrgs(){
        try {
            List<OrganizationResponse> res = new ArrayList<>();
            List<Organization> orgs = organizationRepository.findAll();
            for(Organization org: orgs){
                OrganizationResponse orgres = new OrganizationResponse();
                BeanUtils.copyProperties(org, orgres);
                res.add(orgres);
            }

            return res;
        }catch (Exception e){
            throw new ResourceNotFoundException("No Organizations Found: Create One.");
        }
    }

    // third Service: Get Organization by id
    public OrganizationResponse getOrgById(Long id){
        try {
            Organization org = organizationRepository.findById(id);
            OrganizationResponse res = new OrganizationResponse();
            BeanUtils.copyProperties(org, res);
            return res;
        }catch (Exception e){
            throw new ResourceNotFoundException("Organization not found with id: " + id);
        }
    }

    // fourth Service: Update Organization
    public OrganizationResponse updateOrg(Long id, OrganizationRequest request){
        try {
            Organization org = organizationRepository.findById(id);

            // update fields
            org.setName(request.getName());
            org.setAddress(request.getAddress());

            Organization updatedOrg = organizationRepository.update(org);

            OrganizationResponse res = new OrganizationResponse();
            BeanUtils.copyProperties(updatedOrg, res);


            return res;
        }catch (Exception e){
            throw new ResourceNotFoundException("Organization not found with id: " + id);
        }
    }


    // Fifth Service: Delete Organization
    public String deleteOrg(Long id){
        try{
            Organization org = organizationRepository.findById(id);
            String name = org.getName();
            organizationRepository.delete(id);
            return "Organization: " + name + " deleted successfully";
        }catch (Exception e){
            throw new ResourceNotFoundException("Organization not found with id: " + id);
        }
    }
}
