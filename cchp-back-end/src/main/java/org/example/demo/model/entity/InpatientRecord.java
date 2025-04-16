package org.example.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class InpatientRecord {
    private Integer admissionRecordID;        // 住院记录唯一标识，主键
    private String residentHealthCardID;      // 居民健康卡号，外键
    private String institutionCode;           // 医疗机构组织机构代码
    private String admissionNumber;           // 住院号
    private String admissionConditionCode;    // 入院病情代码
    private BigDecimal totalHospitalizationCost; // 住院总费用
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date diagnosisDate;               // 最近更改时间
} 