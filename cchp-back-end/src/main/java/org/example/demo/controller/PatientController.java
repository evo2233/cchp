package org.example.demo.controller;

import org.example.demo.common.ArgumentResolver;
import org.example.demo.model.CommonResponse;
import org.example.demo.model.dto.PatientLoginDTO;
import org.example.demo.model.dto.PatientRegistrationDTO;
import org.example.demo.model.dto.PatientInfoVO;
import org.example.demo.model.entity.CourseRecordDetail;
import org.example.demo.model.entity.InpatientRecord;
import org.example.demo.service.InpatientService;
import org.example.demo.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientservice;

    private final InpatientService inpatientservice;

    @Autowired
    private PatientController(PatientService _patientService, InpatientService _inpatientService) {
        this.patientservice = _patientService;
        this.inpatientservice = _inpatientService;
    }

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

    // 根据条件查询住院记录 指定identity
    @GetMapping("/inpatient-records")
    public ResponseEntity<List<InpatientRecord>> selectInpatientRecords(
            @RequestParam(required = false) String institutionCode,
            @RequestParam(required = false) String residentHealthCardID,
            @RequestParam(required = false) String diagnosisDate,
            @ArgumentResolver.PatientIdentity String identity) {
        List<InpatientRecord> records = inpatientservice.selectInpatientRecords(
                institutionCode, identity, diagnosisDate);
        return ResponseEntity.ok(records);
    }

    // 根据住院记录ID查询病程记录
    @GetMapping("/course-records/{admissionRecordID}")
    public ResponseEntity<List<CourseRecordDetail>> getCourseRecordDetails(
            @PathVariable Integer admissionRecordID) {
        List<CourseRecordDetail> details = inpatientservice.getCourseRecordDetails(admissionRecordID);
        return ResponseEntity.ok(details);
    }
}
