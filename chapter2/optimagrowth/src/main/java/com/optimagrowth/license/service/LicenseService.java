package com.optimagrowth.license.service;

import com.netflix.discovery.converters.Auto;
import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import com.optimagrowth.license.service.client.OrganizationRestTemplateClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
public class LicenseService {

    @Autowired
    MessageSource messages;

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestTemplateClient;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;

    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

    public License getLicense(String licenseId, String organizationId) throws IllegalAccessException {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

        if(null == license){
            throw new IllegalAccessException(String.format(
                    messages.getMessage("license.search.error.message", null, null),
                    licenseId, organizationId));
        }

        return license.withComment(config.getProperty());
    }

    public License createLicense(License license){
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);

        return license.withComment(config.getProperty());
    }

    public License updateLicense(License license){
        licenseRepository.save(license);

        return license.withComment(config.getProperty());
    }

    public String deleteLicense(String licenseId){
        String responseMessage = null;

        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);

        responseMessage = String.format(messages.getMessage("license.delete.message", null, null), licenseId);

        return responseMessage;
    }

    public License getLicense(String licenseId, String organizationId, String clientType){
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

        if(null == license) {
            throw new IllegalArgumentException(String.format(
                    messages.getMessage("license.search.error.message", null, null),
                    licenseId,
                    organizationId
            ));
        }

        Organization organization = retrieveOrganizationInfo(organizationId, clientType);

        if(null != organization){
            license.setOrganizationId(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(config.getProperty());
    }

    //서비스 디스커버리로 조직 서비스를 찾아 데이터를 얻어온다
    private Organization retrieveOrganizationInfo(String organizationId, String clientType){
        Organization organization = null;

        switch(clientType){
            case "feign":
                System.out.println("i am using the feign client");

                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("i am using the rest client");

                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("i am using the discovery client");

                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestTemplateClient.getOrganization(organizationId);
                break;
        }

        return organization;
    }
    
    //Resilience4J 회로 차단기를 사용, CircuitBreaker로 래핑
    //실패한 모든 호출 시도를 가로챈다
    @CircuitBreaker(name = "licenseService")
    public List<License> getLicensesByOrganization(String organizationId){
        randomlyRunLong();

        return licenseRepository.findByOrganizationId(organizationId);
    }

    //DB 통신 장애를 가정하기 위한 메서드
    private void randomlyRunLong(){
        Random rand = new Random();
        int randomNum = rand.nextInt(3) + 1;

        if(randomNum == 3){
            sleep();
        }
    }

    private void sleep(){
        try{
            Thread.sleep(5000);

            throw new java.util.concurrent.TimeoutException();
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
