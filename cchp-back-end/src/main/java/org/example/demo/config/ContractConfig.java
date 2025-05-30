package org.example.demo.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ContractConfig {
    private String patientContractAddress = "";
    private String institutionContractAddress = "";
    private String inpatientContractAddress = "";
}
