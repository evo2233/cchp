package org.example.demo.controller;

import org.example.demo.model.CommonResponse;
import org.example.demo.model.dto.InstitutionDTO;
import org.example.demo.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final InstitutionService institutionService;

    @Autowired
    public DoctorController(InstitutionService institutionService) { this.institutionService = institutionService; }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse> login(@Valid @RequestBody InstitutionDTO institution) {
        try {
            String token = institutionService.getToken(institution);
            return ResponseEntity.ok(CommonResponse.ok(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }
}
