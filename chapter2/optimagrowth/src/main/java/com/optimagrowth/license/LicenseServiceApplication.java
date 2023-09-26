package com.optimagrowth.license;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.events.model.OrganizationChangeModel;
import com.optimagrowth.license.utils.UserContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
//구성 정보를 다시 읽게 만드는 /refresh 엔드포인트 생성
//사용자 정의 프로퍼티만 다시 읽음 -> DB 접속정보 구성정보와 같은 사용자가 정의하지 않은 항목은 갱신하지 않음
@RefreshScope
//유레카 DiscoveryClient 활성화
@EnableDiscoveryClient
//Feign 클라이언트 활성화
@EnableFeignClients
//유입되는 메시지를 수신하고자 Sink 인터페이스에 정의된 채널을 사용하도록 서비스 설정
@EnableBinding(Sink.class)
public class LicenseServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(LicenseServiceApplication.class);

    @Autowired
    private ServiceConfig serviceConfig;

    //    입력 채널에서 메시지를 받을 때마다 이 메서드를 실행
    //싱크 = 유입되는 각 메시지를 처리
    @StreamListener(Sink.INPUT)
    public void loggerSink(OrganizationChangeModel orgChange){
        logger.debug("Received {} event for the organization id {}", orgChange.getAction(), orgChange.getOrganizationId());
    }

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
        RestTemplate template = new RestTemplate();

        List interceptors = template.getInterceptors();

//        RestTemplate객체에 interceptor 추가
        if(interceptors == null){
//            인터셉터가 아예 없다면 리스트를 만들어 추가
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        }else{
//            있다면 인터셉터들 묶음에 추가
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }

    //레디스 서버에 대한 데이터베이스 커넥션 설정
    @Bean
    JedisConnectionFactory jedisConnectionFactory(){
        String hostname = serviceConfig.getRedisServer();
        int port = Integer.parseInt(serviceConfig.getRedisPort());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostname, port);

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    //레디스 서버에 액션을 실행할ㅇ redisTemplate생성
    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());

        return template;
    }
}
