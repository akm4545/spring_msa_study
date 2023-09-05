package com.optimagrowth.license;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
//구성 정보를 다시 읽게 만드는 /refresh 엔드포인트 생성
//사용자 정의 프로퍼티만 다시 읽음 -> DB 접속정보 구성정보와 같은 사용자가 정의하지 않은 항목은 갱신하지 않음
@RefreshScope
//유레카 DiscoveryClient 활성화
@EnableDiscoveryClient
//Feign 클라이언트 활성화
@EnableFeignClients
public class LicenseServiceApplication {

    public static void main(String[] args){
        SpringApplication.run(LicenseServiceApplication.class, args);
    }

    @Bean
    public LocaleResolver localeResolver(){
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);

        return localeResolver;
    }

    @Bean
    public ResourceBundleMessageSource messageSource(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setBasename("messages");

        return messageSource;
    }

    //이렇게 설정을 했다면 등록된 RestTemplate 빈을 통해 호출 시 Eureka Server에 eureka-client로 등록된 모든 클라이언트에 대해 Load Balancing이 가능하다. 클라이언트 측에서는 필요한 어플리케이션의 이름만 알고 있으면 된다.
    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
