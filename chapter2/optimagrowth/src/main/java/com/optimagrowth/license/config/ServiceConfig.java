package com.optimagrowth.license.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//@Configuration
//config 서버에서 example 프로퍼티를 가져와 세팅
//@ConfigurationProperties(prefix = "example")
@Getter
@Setter
@Component
public class ServiceConfig {

    private String property;

    @Value("${redis.server}")
    private String redisServer = "";

    @Value("${redis.port}")
    private String redisPort = "";
}
