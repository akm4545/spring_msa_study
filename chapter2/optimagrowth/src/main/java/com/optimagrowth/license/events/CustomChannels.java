package com.optimagrowth.license.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {

    //채널 이름 지정
    @Input("inboundOrgChanges")
    //input 애너테이션으로 노출된 채널에 대한 SubscribableChannel 클래스 반환 
    SubscribableChannel orgs();
}

