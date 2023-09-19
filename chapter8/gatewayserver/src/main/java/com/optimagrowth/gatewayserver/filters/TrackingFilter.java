package com.optimagrowth.gatewayserver.filters;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
//글로벌 필터는 GlobalFilter 인터페이스를 구현하고 filter() 메서드를 재정의 해야한다
public class TrackingFilter implements GlobalFilter {
    private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    @Autowired
    FilterUtils filterUtils;

//    요청이 필터를 통과할때마다 실행되는 코드
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
//        exchange로 header 추출
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

        if(isCorrelationIdPresent(requestHeaders)){
            logger.debug("tmx-correlation-id found in tracking filter: {}. ", filterUtils.getCorrelationId(requestHeaders));
        }else{
            String correlationID = generateCorrelationId();
            exchange = filterUtils.setCorrelationId(exchange, correlationID);

            logger.debug("tmx-correlation-id generated in traking filter: {}.", correlationID);
        }

        System.out.println("The authentication name from the token is: " + getUserName(requestHeaders));

        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders){
        if(filterUtils.getCorrelationId(requestHeaders) != null){
            return true;
        }else{
            return false;
        }
    }

    private String generateCorrelationId(){
        return java.util.UUID.randomUUID().toString();
    }

    private String getUserName(HttpHeaders requestHeaders){
        String username = "";

        if(filterUtils.getAuthToken(requestHeaders) != null){
            String authToken = filterUtils.getAuthToken(requestHeaders).replace("Bearer ", "");
            JSONObject jsonObj = decodeJWT(authToken);

            try{
                username = jsonObj.getString("preferred_username");
            }catch (Exception e){
                logger.debug(e.getMessage());
            }
        }

        return username;
    }

    private JSONObject decodeJWT(String JWTToken){
        String[] split_string = JWTToken.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));
        JSONObject jsonObj = new JSONObject(body);

        return jsonObj;
    }
}
