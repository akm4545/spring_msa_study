package com.optimagrowth.organizationservice.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrganizationChangeModel {
    private String type;
    
    //이벤트를 발생시킨 액션
    private String action;

    //이벤트와 연관된 조직 Id
    private String organizationId;

    //이벤트를 발생시킨 서비스 호출의 상관관계 ID 
    //메시지 흐름을 추적하고 디버깅하는데 크게 도움이 되므로 이벤트에 꼭 포함시켜야 한다
    private String correlationId;

    public OrganizationChangeModel(String type, String action, String organizationId, String correlationId){
        super();

        this.type = type;
        this.action = action;
        this.organizationId = organizationId;
        this.correlationId = correlationId;
    }
}
