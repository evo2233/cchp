package org.example.demo.service;

import org.example.demo.model.dto.PatientLoginDTO;
import org.example.demo.model.dto.PatientRegistrationDTO;
import org.example.demo.model.vo.PatientInfoVO;

public interface PatientService {
    void addPatient(PatientRegistrationDTO patientdto) throws Exception;

    String getToken(PatientLoginDTO patientdto) throws Exception;

    PatientInfoVO getInfo(String identity) throws Exception;
}
