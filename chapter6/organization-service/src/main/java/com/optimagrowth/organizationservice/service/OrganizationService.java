package com.optimagrowth.organizationservice.service;

import brave.ScopedSpan;
import brave.Tracer;
import com.optimagrowth.organizationservice.events.source.SimpleSourceBean;
import com.optimagrowth.organizationservice.model.Organization;
import com.optimagrowth.organizationservice.repository.OrganizationRepository;
import com.optimagrowth.organizationservice.utils.ActionEnum;
import org.apache.kafka.common.errors.IllegalGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    @Autowired
    private OrganizationRepository repository;

    @Autowired
    SimpleSourceBean simpleSourceBean;

    @Autowired
    Tracer tracer;
    
    //조직 데이터를 변경하는 모든 메서드는 메시지를 발행한다
    public Organization findById(String organizationId){
        Optional<Organization> opt = null;
        ScopedSpan newSpan = tracer.startScopedSpan("getOrgDBCall");

        try{
            opt = repository.findById(organizationId);
            simpleSourceBean.publishOrganizationChange(ActionEnum.GET, organizationId);

            if(!opt.isPresent()){
                String message = String.format("Unable to find an organization with the Organization id %s", organizationId);
                logger.error(message);

                throw new IllegalGenerationException(message);
            }

            logger.debug("Retrieving Organization info: " + opt.get().toString());
        }finally {
            newSpan.tag("peer.service", "postgres");
            newSpan.annotate("Client received");
            newSpan.finish();
        }

        return opt.get();
    }

//    public Organization findById(String organizationId){
//        Optional<Organization> opt = repository.findById(organizationId);
//
//        simpleSourceBean.publishOrganizationChange(ActionEnum.GET, organizationId);
//
//        return (opt.isPresent()) ? opt.get() : null;
//    }

//    public Organization findById(String organizationId){
//        Optional<Organization> opt = repository.findById(organizationId);
//
//        return (opt.isPresent()) ? opt.get() : null;
//    }

    public Organization create(Organization organization){
        organization.setId(UUID.randomUUID().toString());
        organization = repository.save(organization);

        simpleSourceBean.publishOrganizationChange(ActionEnum.CREATED,
                organization.getId());

        return organization;
    }

//    public Organization create(Organization organization){
//        organization.setId(UUID.randomUUID().toString());
//        organization = repository.save(organization);
//
//        return organization;
//    }

    public void update(Organization organization){
        repository.save(organization);

        simpleSourceBean.publishOrganizationChange(ActionEnum.UPDATED, organization.getId());
    }

//    public void update(Organization organization){
//        repository.save(organization);
//    }

    public void delete(String organizationId){
        repository.deleteById(organizationId);

        simpleSourceBean.publishOrganizationChange(ActionEnum.DELETED, organizationId);
    }

//    public void delete(Organization organization){
//        repository.deleteById(organization.getId());
//    }
}
