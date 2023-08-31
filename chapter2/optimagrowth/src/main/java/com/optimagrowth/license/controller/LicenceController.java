package com.optimagrowth.license.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping(value = "v1/organization/{organizationId}/license")
public class LicenceController {

    @Autowired
    private LicenseService licenseService;

    @GetMapping(value = "/{licenseId}")
    public ResponseEntity<License> getLicense(@PathVariable("organizationId") String organizationId, @PathVariable("licenseId") String licenseId){
        License license = licenseService.getLicense(licenseId, organizationId);

        license.add(linkTo(methodOn(LicenceController.class)
                    .getLicense(organizationId, license.getLicenseId()))
                .withSelfRel(),
                //루트 매핑 획득
                linkTo(
                        //메서드 매핑 획득
                        methodOn(LicenceController.class)
                        .createLicense(organizationId, license, null))
                .withRel("createLicense"),
                linkTo(methodOn(LicenceController.class)
                        .updateLicense(organizationId, license))
                .withRel("updateLicense"),
                linkTo(methodOn(LicenceController.class)
                        .deleteLicense(organizationId, license.getLicenseId()))
                .withRel("deleteLicense")
        );

        return ResponseEntity.ok(license);
    }

    @PutMapping
    public ResponseEntity<String> updateLicense(@PathVariable("organizationId") String organizationId, @RequestBody License request){
        return ResponseEntity.ok(licenseService.updateLicense(request, organizationId));
    }

    @PostMapping
    public ResponseEntity<String> createLicense(@PathVariable("organizationId") String organizationId,
                                                @RequestBody License request,
                                                @RequestHeader(value = "Accept-Language", required = false) Locale locale){
        return ResponseEntity.ok(licenseService.createLicense(request, organizationId, locale));
    }

    @DeleteMapping(value = "/{licenseId}")
    public ResponseEntity<String> deleteLicense(@PathVariable("organizationId") String organizationId, @PathVariable("licenseId")String licenseId){
        return ResponseEntity.ok(licenseService.deleteLicense(licenseId, organizationId));
    }
}
