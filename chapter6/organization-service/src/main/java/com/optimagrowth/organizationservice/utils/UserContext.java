package com.optimagrowth.organizationservice.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    public static final String CORRELATION_ID = "tmx-correlation-id";
    public static final String AUTH_TOKEN = "tmx-auth-token";
    public static final String USER_ID = "tmx-user-id";
    public static final String ORGANIZATION_ID = "tmx-organization-id";

//    private String correlationId = new String();
//
//    private String authToken = new String();
//
//    private String userId = new String();
//
//    private String organizationId = new String();

    //변수를 ThreadLocal에 저장하면 현재 스레드에 대한 데이터를 스레드별로 저장할 수 있다
    //여기 서렂어된 정보는 그 값을 설정한 스레드만 읽을 수 있다.
    private static final ThreadLocal<String> correlationId = new ThreadLocal<String>();
    private static final ThreadLocal<String> authToken = new ThreadLocal<String>();
    private static final ThreadLocal<String> userId = new ThreadLocal<String>();
    private static final ThreadLocal<String> orgId = new ThreadLocal<String>();

    public static String getCorrelationId(){
        return correlationId.get();
    }

    public static void setCorrelationId(String cid){
        correlationId.set(cid);
    }

    public static String getAuthToken(){
        return authToken.get();
    }

    public static void setAuthToken(String aToken){
        authToken.set(aToken);
    }

    public static String getUserId(){
        return userId.get();
    }

    public static void setUserId(String aUser){
        userId.set(aUser);
    }

    public static String getOrgId(){
        return orgId.get();
    }

    public static void setOrgId(String aOrg){
        orgId.set(aOrg);
    }

    public static HttpHeaders getHttpHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.set(CORRELATION_ID, getCorrelationId());

        return httpHeaders;
    }

//    public String getCorrelationId(){
//        return correlationId;
//    }
//
//    public void setCorrelationId(String correlationId){
//        this.correlationId = correlationId;
//    }
//
//    public String getAuthToken(){
//        return authToken;
//    }
//
//    public void setAuthToken(String authToken){
//        this.authToken = authToken;
//    }
//
//    public String getUserId(){
//        return userId;
//    }
//
//    public void setUserId(String userId){
//        this.userId = userId;
//    }
//
//    public String getOrganizationId(){
//        return organizationId;
//    }
//
//    public void setOrganizationId(String organizationId){
//        this.organizationId = organizationId;
//    }
}
