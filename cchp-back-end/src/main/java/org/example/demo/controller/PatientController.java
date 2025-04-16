package org.example.demo.controller;

import org.example.demo.authentication.ArgumentResolver;
import org.example.demo.model.CommonResponse;
import org.example.demo.model.dto.PatientLoginDTO;
import org.example.demo.model.dto.PatientRegistrationDTO;
import org.example.demo.model.vo.PatientInfoVO;
import org.example.demo.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientservice;

    @Autowired
    private PatientController(PatientService _patientService) { this.patientservice = _patientService; }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse> patientRegister(
            @Valid @RequestBody PatientRegistrationDTO patientRegistrationDTO
    ){
        try{
            patientservice.addPatient(patientRegistrationDTO);
            return ResponseEntity.ok(CommonResponse.ok("Patient "+patientRegistrationDTO.getIdentity()+" registration successful"));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse> patientLogin(
            @Valid @RequestBody PatientLoginDTO patientLoginDTO
    ){
        try{
            String token = patientservice.getToken(patientLoginDTO);
            return ResponseEntity.ok(CommonResponse.ok(token));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<CommonResponse> getPatientInfo(@ArgumentResolver.PatientIdentity String identity) {
        try {
            PatientInfoVO patientinfo = patientservice.getInfo(identity);
            return ResponseEntity.ok(CommonResponse.ok(patientinfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }

}
