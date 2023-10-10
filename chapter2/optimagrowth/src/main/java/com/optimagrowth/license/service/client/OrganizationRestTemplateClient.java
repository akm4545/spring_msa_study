package com.optimagrowth.license.service.client;

import brave.Tracer;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.OrganizationRedisRepository;
import com.optimagrowth.license.utils.UserContext;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {
    
    //로드벨런싱을 적용한 restTemplate를 빈으로 생성했으므로 주입하여 사용
//    @Autowired
//    RestTemplate restTemplate;

//    표준 RestTemplate에 대한 드롭인 대체룸, 액세스 토큰의 전파 처리
    @Autowired
    private KeycloakRestTemplate restTemplate;

    //스프링 클라우드 슬루스 추적 정보 엑세스 용
    @Autowired
    Tracer tracer;

    @Autowired
    OrganizationRedisRepository redisRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    private Organization checkRedisCache(String organizationId){
        try {
            //조직Id 로 레디스에서 조회
            return redisRepository
                    .findById(organizationId)
                    .orElse(null);
        }catch (Exception e){
            logger.error("Error encountered while trying to retrieve organization {} check Redis Cache. Exception {}", organizationId, e);

            return null;
        }
    }

    //레디스 저장
    private void cacheOrganizationObject(Organization organization) {
        try {
            redisRepository.save(organization);
        } catch (Exception e) {
            logger.error("Unable to cache organization {} in Redis. Exception {}", organization.getId(), e);
        }
    }

    public Organization getOrganization(String organizationId){
        //서비스 Id로 url 생성
        //로드벨런싱 적용 restTemplate 는 url을 파싱하고 서버 이름으로 전달된 것을 키로 사용한다.
        //해당 키로 서비스의 인스턴스를 로드 벨런서에 쿼리한다.
        //라운드 로빈 방식으로 부하 분산
//        ResponseEntity<Organization> restExchange = restTemplate.exchange(
//          "http://organization-service/v1/organization/{organizationId}",
//                HttpMethod.GET,
//                null,
//                Organization.class,
//                organizationId
//        );
        logger.debug("In Licensing Service.getOrganization: {}", UserContext.getOrganizationId());

        Organization organization = checkRedisCache(organizationId);

        //레디스에서 조회가 가능하면 레디스 데이터 리턴
        if(organization != null){
            logger.debug("I have successfully retrieved an organization {} from the redis cache: {}", organizationId, organization);

            return organization;
        }

        logger.debug("Unable to locate organization from the redis cache: {}.", organizationId);


//        게이트웨이 호출
        ResponseEntity<Organization> restExchange = restTemplate.exchange(
          "http://localhost:8072/organization/v1/organization/{organizationId}",
                HttpMethod.GET,
                null,
                Organization.class,
                organizationId
        );

        organization = restExchange.getBody();

        //레디스에서 조회가 불가능하면 조직 서비스에서 데이터를 얻어와 레디스에 저장
        if(organization != null) {
            cacheOrganizationObject(organization);
        }
        
        return restExchange.getBody();
    }
}
