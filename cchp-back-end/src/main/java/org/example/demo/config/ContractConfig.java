package org.example.demo.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ContractConfig {
    private String helloWorldAddress = "";
    private String patientAddress = "";
}
