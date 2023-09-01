package com.optimagrowth.license.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
//config 서버에서 example 프로퍼티를 가져와 세팅
@ConfigurationProperties(prefix = "example")
@Getter
@Setter
public class ServiceConfig {

    private String property;
}
