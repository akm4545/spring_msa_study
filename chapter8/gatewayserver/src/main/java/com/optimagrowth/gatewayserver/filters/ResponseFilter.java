package com.optimagrowth.gatewayserver.filters;

import brave.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.logging.Filter;

@Configuration
public class ResponseFilter {
    final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

//   추적 ID 및 스팬 ID 정보에 접근하는 진입점 설정
    @Autowired
    Tracer tracer;

    @Autowired
    FilterUtils filterUtils;

//    @Bean
//    public GlobalFilter postGlobalFilter(){
//        return (exchange, chain) -> {
//           return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//               HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
////               원본 http 요청에 전달된 상관관계 ID 를 가져온다
//               String correlationId = filterUtils.getCorrelationId(requestHeaders);
//
//               logger.debug("Adding the correlation id to the outbound headers. {}", correlationId);
//
//               //응답에 상관관계 id를 삽입한다.
//               exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);
//
////               발신 요청 URI 로깅
//               logger.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
//
//            }));
//        };
//    }

    @Bean
    public GlobalFilter postGlobalFilter(){
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                //슬루스 추적 Id의 응답 헤더 tmx-correlation-id 에 스팬 추가
                String traceId = tracer.currentSpan()
                        .context()
                        .traceIdString();

                logger.debug("Adding the correlation id to the outbound headers. {}", traceId);

                exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);

                logger.debug("Completing outgoing request for . {}", exchange.getRequest().getURI());
            }));
        };
    }
}
