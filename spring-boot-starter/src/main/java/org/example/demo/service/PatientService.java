package org.example.demo.service;

import org.example.demo.model.dto.PatientRegistrationDTO;
import org.example.demo.model.vo.PatientInfoVO;

public interface PatientService {
    void registerPatient(PatientRegistrationDTO patientRegistrationDTO) throws Exception;
    
    PatientInfoVO getPatientInfo(String identityFile) throws Exception;
}
