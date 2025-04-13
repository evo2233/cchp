package org.example.demo.controller;

import org.example.demo.model.CommonResponse;
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

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse> registerPatient(
            @Valid @RequestBody PatientRegistrationDTO patientRegistrationDTO) {
        try{
            patientService.registerPatient(patientRegistrationDTO);
            return ResponseEntity.ok(CommonResponse.ok("用户"+patientRegistrationDTO.getIdentity()+"注册成功"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }

    @GetMapping("/info/{identity}")
    public ResponseEntity<CommonResponse> getPatientInfo(@PathVariable String identity) {
        try {
            PatientInfoVO patientInfo = patientService.getPatientInfo(identity);
            return ResponseEntity.ok(CommonResponse.ok(patientInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("400", e));
        }
    }
}
