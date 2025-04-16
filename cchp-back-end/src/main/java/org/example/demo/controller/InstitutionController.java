package org.example.demo.controller;

import org.example.demo.model.bo.InstitutionRegisterInputBO;
import org.example.demo.model.dto.InstitutionDTO;
import org.example.demo.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/institution")
public class InstitutionController {

    private final InstitutionService institutionService;

    @Autowired
    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @PostMapping("/authorize")
    public boolean authorizeInstitution(
            @RequestBody InstitutionRegisterInputBO input,
            @RequestParam String adminAddress) {
        return institutionService.authorizeInstitution(input, adminAddress);
    }

    @PostMapping("/revoke")
    public boolean revokeInstitution(
            @RequestParam String institutionCode,
            @RequestParam String adminAddress) {
        return institutionService.revokeInstitution(institutionCode, adminAddress);
    }

    @GetMapping("/all")
    public List<InstitutionDTO> getAllAuthorizedInstitutions() {
        return institutionService.getAllAuthorizedInstitutions();
    }

    @GetMapping("/check")
    public boolean isAuthorized(@RequestParam String institutionCode) {
        return institutionService.isAuthorized(institutionCode);
    }

    @GetMapping("/check-address")
    public boolean isAuthorizedAddress(@RequestParam String institutionAddress) {
        return institutionService.isAuthorizedAddress(institutionAddress);
    }

    @GetMapping("/code")
    public String getInstitutionCode(@RequestParam String institutionAddress) {
        return institutionService.getInstitutionCode(institutionAddress);
    }
}
