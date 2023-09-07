package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import com.optimagrowth.license.service.client.OrganizationRestTemplateClient;
import com.optimagrowth.license.utils.UserContextHolder;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

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
    //fallbackMethod = 서비스 호출이 실패할 때 호출되는 함수 정의
    //폴백 메서드가 다른 서비스를 호출한다면 해당 서비스 호출 메서드도 서킷브레이커로 보호해주는게 좋다
    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    //벌크헤드 패턴 적용 기본값은 세마포어 벌크헤드    
    @Bulkhead(name = "bulkheadLicenseService", fallbackMethod = "buildFallbackLicenseList")
    //스레드풀 방식 벌크해드 패턴 적용    
//    @Bulkhead(name = "bulkheadLicenseService", fallbackMethod = "buildFallbackLicenseList", type=Bulkhead.Type.THREADPOOL)
    public List<License> getLicensesByOrganization(String organizationId){
        io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("licenseService");
        System.out.println(circuitBreaker.getName());
        System.out.println(circuitBreaker.getCircuitBreakerConfig());

        logger.debug("getLicensesByOrganization Correlation id: {}", UserContextHolder.getContext().getCorrelationId());

        randomlyRunLong();

        return licenseRepository.findByOrganizationId(organizationId);
    }

    //풀백 메서드
    //서킷 브레이커 메서드랑 같은 위치에 존재해야함
    //원본 메서드 처럼 매개변수가 동일해야 한다
    private List<License> buildFallbackLicenseList(String organizationId, Throwable t){
        List<License> fallbackList = new ArrayList<>();
        License license = new License();

        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName("Sorry no licensing information currently available");

        fallbackList.add(license);

        return fallbackList;
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
