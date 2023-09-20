package com.optimagrowth.organizationservice.events.source;

import com.optimagrowth.organizationservice.events.model.OrganizationChangeModel;
import com.optimagrowth.organizationservice.utils.ActionEnum;
import com.optimagrowth.organizationservice.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Component
public class SimpleSourceBean {
    private Source source;

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    //서비스에서 사용되는 Source 인터페이스 구현체 주입
    @Autowired
    public SimpleSourceBean(Source source){
        this.source = source;
    }

    public void publishOrganizationChange(ActionEnum action, String organizationId){
        logger.debug("Sending Kafka message {} for Organization Id: {}", action, organizationId);

        //메시지 발행
        OrganizationChangeModel change = new OrganizationChangeModel(
                OrganizationChangeModel.class.getTypeName(),
                action.toString(),
                organizationId,
                UserContext.getCorrelationId()
        ); 

        //Source 클래스에서 정의된 채널에서 전달된 메시지를 발행
        //Source 인터페이스는 서비스를 하나의 채널에서만 발행할 때 편리
        //메시지 브로커에 메시지를 전달
        source.output().send(MessageBuilder.withPayload(change).build());
    }
}
