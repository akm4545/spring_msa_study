package com.optimagrowth.organizationservice.service;

import com.optimagrowth.organizationservice.events.source.SimpleSourceBean;
import com.optimagrowth.organizationservice.model.Organization;
import com.optimagrowth.organizationservice.repository.OrganizationRepository;
import com.optimagrowth.organizationservice.utils.ActionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository repository;

    @Autowired
    SimpleSourceBean simpleSourceBean;

    public Organization findById(String organizationId){
        Optional<Organization> opt = repository.findById(organizationId);

        simpleSourceBean.publishOrganizationChange(ActionEnum.GET, organizationId);

        return (opt.isPresent()) ? opt.get() : null;
    }

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
