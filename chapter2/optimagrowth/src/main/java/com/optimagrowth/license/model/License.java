package com.optimagrowth.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@ToString
//HATEOAS 사용 다음단계의 api 정보를 제공하기 위한 준비
public class License extends RepresentationModel<License> {
    private int id;

    private String licenseId;

    private String description;

    private String organizationId;

    private String productName;

    private String licenseType;
}
