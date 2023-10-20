package com.optimagrowth.license.events.handler;

import com.optimagrowth.license.events.CustomChannels;
import com.optimagrowth.license.events.model.OrganizationChangeModel;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.OrganizationRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

//메세지 처리 클래스 변경
@EnableBinding(CustomChannels.class)
public class OrganizationChangeHandler {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);

    private OrganizationRedisRepository organizationRedisRepository;

    //메타데이터 추출 작업
    @StreamListener("inboundOrgChanges")
    public void loggerSink(OrganizationChangeModel organization){
        logger.debug("Received a message of type " + organization.getType());

        System.out.println(organization.getAction());

        //데이터의 액션에 따라 처리
        switch(organization.getAction()){
            case "GET":
                logger.debug("Received a GET event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            case "SAVE":
                logger.debug("Received a SAVE event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            case "UPDATE":
                logger.debug("Received a UPDATE event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            case "DELETED":
                logger.debug("Received a DELETE event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            default:
                logger.error("Received an UNKOWN event from the organization service of type {}", organization.getType());
                break;
        }
    }
}
