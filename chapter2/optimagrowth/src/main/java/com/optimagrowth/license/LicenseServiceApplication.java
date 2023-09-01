package com.optimagrowth.license;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
//구성 정보를 다시 읽게 만드는 /refresh 엔드포인트 생성
//사용자 정의 프로퍼티만 다시 읽음 -> DB 접속정보 구성정보와 같은 사용자가 정의하지 않은 항목은 갱신하지 않음
@RefreshScope
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
}
