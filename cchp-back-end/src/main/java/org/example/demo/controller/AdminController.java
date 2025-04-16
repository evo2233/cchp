package org.example.demo.controller;


import org.example.demo.model.CommonResponse;
import org.example.demo.model.dto.AdminLoginDTO;
import org.example.demo.model.dto.InstitutionDTO;
import org.example.demo.service.AdminService;
import org.example.demo.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    private final AdminService adminService;

    private final InstitutionService institutionService;

    @Autowired
    public AdminController(AdminService adminService, InstitutionService institutionService) {
        this.adminService = adminService;
        this.institutionService = institutionService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<CommonResponse> login(@RequestBody AdminLoginDTO admindto) {
        try {
            String address = adminService.login(admindto.getUsername(), admindto.getPassword());
            return ResponseEntity.ok(CommonResponse.ok("address："+address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }

    @PostMapping("/authorize")
    public boolean authorizeInstitution(
            @RequestBody InstitutionDTO input,
            @RequestParam String adminAddress) {
        return institutionService.authorizeInstitution(input, adminAddress);
    }

    @PostMapping("/revoke")
    public boolean revokeInstitution(
            @RequestParam String institutionCode,
            @RequestParam String adminAddress) {
        return institutionService.revokeInstitution(institutionCode, adminAddress);
    }

    @GetMapping("/all-institution")
    public List<InstitutionDTO> getAllAuthorizedInstitutions(@RequestParam String adminAddress) {
        return institutionService.getAllAuthorizedInstitutions();
    }

    @GetMapping("/check")
    public boolean isAuthorized(@RequestParam String institutionCode) {
        return institutionService.isAuthorized(institutionCode);
    }
} 