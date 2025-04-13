package org.example.demo.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegisterInputBO {
    private String userAddr;
    private String realName;
    private String idCard;

    public List<Object> toArgs() {
        List<Object> args = new ArrayList<>();
        args.add(userAddr);
        args.add(realName);
        args.add(idCard);
        return args;
    }
}