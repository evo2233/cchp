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
    private String identity;
    private String realname;
    private String gendercode;
    private String birthdate;

    public List<Object> toArgs() {
        List<Object> args = new ArrayList<>();
        args.add(userAddr);
        args.add(identity);
        args.add(realname);
        args.add(gendercode);
        args.add(birthdate);
        return args;
    }
}
