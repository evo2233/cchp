package org.example.demo.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionRegisterInputBO {

    private String institutionAddr;
    private String institutionCode;
    private String institutionName;

    public List<Object> toArgs() {
        List<Object> args = new ArrayList<>();
        args.add(institutionAddr);
        args.add(institutionCode);
        args.add(institutionName);
        return args;
    }

}
