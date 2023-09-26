package com.optimagrowth.organizationservice;

import com.optimagrowth.organizationservice.utils.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
@RefreshScope
//스프링 클라우드 스트림이 메시지 브로커에 애플리케이션을 바인딩하도록 지정
//Source 클래스에서 정의된 채널들을 이용하여 메시지 브로커와 통신할 것이라고 알림
//Source = 메시지를 발행하려고 조직 서비스가 내부적으로 사용하는 빈 이름
@EnableBinding(Source.class)
public class OrganizationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrganizationServiceApplication.class, args);
	}

	@Bean
	public ResourceBundleMessageSource messageSource(){
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setBasename("messages");

		return messageSource;
	}

	@LoadBalanced
	@Bean
	public RestTemplate getRestTemplate(){
		RestTemplate template = new RestTemplate();

		List interceptors = template.getInterceptors();

		if(interceptors == null){
			template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
		}else{
			interceptors.add(new UserContextInterceptor());
			template.setInterceptors(interceptors);
		}

		return template;
	}
}
