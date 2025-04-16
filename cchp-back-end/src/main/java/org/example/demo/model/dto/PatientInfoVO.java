package org.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// return of blockchain
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientInfoVO {
    private String identity;
    private String realname;
    private String gendercode;
    private String birthdate;
}
