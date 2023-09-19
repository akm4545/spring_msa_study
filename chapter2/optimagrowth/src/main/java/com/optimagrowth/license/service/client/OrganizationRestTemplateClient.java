package com.optimagrowth.license.service.client;

import com.optimagrowth.license.model.Organization;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
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

//        게이트웨이 호출
        ResponseEntity<Organization> restExchange = restTemplate.exchange(
          "http://localhost:8072/organization/v1/organization/{organizationId}",
                HttpMethod.GET,
                null,
                Organization.class,
                organizationId
        );
        
        return restExchange.getBody();
    }
}
