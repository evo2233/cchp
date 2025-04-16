package org.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegistrationDTO {
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{17}([0-9]|X|x)$)", message = "身份证号格式不正确")
    private String identity;

    @NotBlank(message = "姓名不能为空")
    private String realname;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^[FM]$", message = "性别代码必须是 F 或 M")
    private String gendercode;

    @NotNull(message = "出生日期不能为空")
    private LocalDate birthdate; // yyyy-MM-dd

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度必须在6-30个字符之间")
    private String password;
}
