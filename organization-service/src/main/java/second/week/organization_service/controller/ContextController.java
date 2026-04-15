package second.week.organization_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import second.week.organization_service.dto.context.ContextResponse;
import second.week.organization_service.service.ContextService;

@RestController
@RequestMapping("/internal")
public class ContextController {

    @Autowired
    private ContextService contextService;

    @GetMapping("/context/{orgId}")
    public ContextResponse getContext(@PathVariable Long orgId){
        return contextService.getContext(orgId);
    }
}
