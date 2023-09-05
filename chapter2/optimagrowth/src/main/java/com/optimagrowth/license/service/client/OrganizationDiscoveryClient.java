package com.optimagrowth.license.service.client;

import com.optimagrowth.license.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OrganizationDiscoveryClient {

    //DiscoveryClient 주입
    @Autowired
    private DiscoveryClient discoveryClient;

    //로드벨런싱 지원 x
    public Organization getOrganization(String organizationId){
        RestTemplate restTemplate = new RestTemplate();

        //서비스 Id로 인스턴스 찾기
        //조직 서비스의 모든 인스턴스 리스트 획득
        List<ServiceInstance> instances = discoveryClient.getInstances("organization-service");

        if(instances.size() == 0) {
            return null;
        }

        //서비스 엔드 포인트 획득
        String serviceUri = String.format("%s/v1/organization/%s", instances.get(0).getUri().toString(), organizationId);

        //서비스 호출
        ResponseEntity<Organization> restExchange = restTemplate.exchange(serviceUri, HttpMethod.GET, null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
