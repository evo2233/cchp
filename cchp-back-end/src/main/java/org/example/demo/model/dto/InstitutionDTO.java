package org.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionDTO {
    @NotBlank(message = "机构代码不能为空")
    private String institutionCode;

    @NotBlank(message = "机构名不能为空")
    private String institutionName;
}
